package com.localllm.localaichatapp.domain.service

import com.localllm.localaichatapp.domain.model.Result
import com.localllm.localaichatapp.domain.model.automation.*
import com.localllm.localaichatapp.domain.repository.AutomationRepository
import kotlinx.coroutines.*
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Core automation engine that executes workflows.
 * Inspired by Zapier's workflow execution engine.
 */
@Singleton
class AutomationEngine @Inject constructor(
    private val automationRepository: AutomationRepository,
    private val conditionEvaluator: ConditionEvaluator,
    private val actionExecutor: ActionExecutor
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val runningExecutions = mutableMapOf<String, Job>()

    /**
     * Execute a workflow by ID.
     */
    suspend fun executeWorkflow(
        workflowId: String,
        context: WorkflowContext
    ): WorkflowResult {
        val workflowResult = automationRepository.getWorkflow(workflowId)

        return when (workflowResult) {
            is Result.Success -> {
                val workflow = workflowResult.data
                if (workflow != null) {
                    executeWorkflow(workflow, context)
                } else {
                    WorkflowResult.Failed(
                        workflowId = workflowId,
                        error = Exception("Workflow not found"),
                        failedAtStep = 0
                    )
                }
            }
            is Result.Error -> {
                WorkflowResult.Failed(
                    workflowId = workflowId,
                    error = workflowResult.exception,
                    failedAtStep = 0
                )
            }
        }
    }

    /**
     * Execute a workflow with given context.
     */
    suspend fun executeWorkflow(
        workflow: Workflow,
        context: WorkflowContext
    ): WorkflowResult = withContext(Dispatchers.Default) {
        if (!workflow.enabled) {
            return@withContext WorkflowResult.Skipped(
                workflowId = workflow.id,
                reason = "Workflow is disabled"
            )
        }

        val executionId = UUID.randomUUID().toString()
        val startTime = System.currentTimeMillis()
        val logs = mutableListOf<WorkflowLog>()

        try {
            logs.add(
                WorkflowLog(
                    timestamp = startTime,
                    level = LogLevel.INFO,
                    message = "Starting workflow execution",
                    step = 0,
                    stepType = "TRIGGER"
                )
            )

            // Evaluate conditions
            if (workflow.conditions.isNotEmpty()) {
                val conditionsPass = evaluateConditions(workflow.conditions, context, logs)

                if (!conditionsPass) {
                    logs.add(
                        WorkflowLog(
                            timestamp = System.currentTimeMillis(),
                            level = LogLevel.INFO,
                            message = "Conditions not met, skipping workflow",
                            step = 0,
                            stepType = "CONDITION"
                        )
                    )

                    recordExecution(
                        workflow.id,
                        executionId,
                        WorkflowStatus.SKIPPED,
                        startTime,
                        logs,
                        context
                    )

                    return@withContext WorkflowResult.Skipped(
                        workflowId = workflow.id,
                        reason = "Conditions not met"
                    )
                }
            }

            // Execute actions
            var actionsExecuted = 0
            workflow.actions.forEach { action ->
                try {
                    logs.add(
                        WorkflowLog(
                            timestamp = System.currentTimeMillis(),
                            level = LogLevel.INFO,
                            message = "Executing action: ${action.type}",
                            step = action.order,
                            stepType = "ACTION"
                        )
                    )

                    actionExecutor.execute(action, context)
                    actionsExecuted++

                    logs.add(
                        WorkflowLog(
                            timestamp = System.currentTimeMillis(),
                            level = LogLevel.INFO,
                            message = "Action completed successfully",
                            step = action.order,
                            stepType = "ACTION"
                        )
                    )
                } catch (e: Exception) {
                    logs.add(
                        WorkflowLog(
                            timestamp = System.currentTimeMillis(),
                            level = LogLevel.ERROR,
                            message = "Action failed: ${e.message}",
                            step = action.order,
                            stepType = "ACTION"
                        )
                    )

                    recordExecution(
                        workflow.id,
                        executionId,
                        WorkflowStatus.FAILED,
                        startTime,
                        logs,
                        context,
                        error = e.message
                    )

                    return@withContext WorkflowResult.Failed(
                        workflowId = workflow.id,
                        error = e,
                        failedAtStep = action.order,
                        partialLogs = logs.map { it.message }
                    )
                }
            }

            val endTime = System.currentTimeMillis()
            val duration = endTime - startTime

            logs.add(
                WorkflowLog(
                    timestamp = endTime,
                    level = LogLevel.INFO,
                    message = "Workflow completed successfully",
                    step = workflow.actions.size,
                    stepType = "COMPLETE"
                )
            )

            // Update execution stats
            automationRepository.updateExecutionStats(workflow.id)

            // Record execution
            recordExecution(
                workflow.id,
                executionId,
                WorkflowStatus.SUCCESS,
                startTime,
                logs,
                context
            )

            WorkflowResult.Success(
                workflowId = workflow.id,
                executionId = executionId,
                actionsExecuted = actionsExecuted,
                duration = duration,
                logs = logs.map { it.message }
            )
        } catch (e: CancellationException) {
            logs.add(
                WorkflowLog(
                    timestamp = System.currentTimeMillis(),
                    level = LogLevel.WARNING,
                    message = "Workflow cancelled",
                    step = 0,
                    stepType = "CANCELLED"
                )
            )

            recordExecution(
                workflow.id,
                executionId,
                WorkflowStatus.CANCELLED,
                startTime,
                logs,
                context
            )

            WorkflowResult.Cancelled(
                workflowId = workflow.id,
                reason = "Execution cancelled"
            )
        } catch (e: Exception) {
            logs.add(
                WorkflowLog(
                    timestamp = System.currentTimeMillis(),
                    level = LogLevel.ERROR,
                    message = "Workflow failed: ${e.message}",
                    step = 0,
                    stepType = "ERROR"
                )
            )

            recordExecution(
                workflow.id,
                executionId,
                WorkflowStatus.FAILED,
                startTime,
                logs,
                context,
                error = e.message
            )

            WorkflowResult.Failed(
                workflowId = workflow.id,
                error = e,
                failedAtStep = 0,
                partialLogs = logs.map { it.message }
            )
        }
    }

    /**
     * Execute workflow asynchronously (fire and forget).
     */
    fun executeWorkflowAsync(
        workflow: Workflow,
        context: WorkflowContext
    ): String {
        val executionId = UUID.randomUUID().toString()

        val job = scope.launch {
            executeWorkflow(workflow, context)
        }

        runningExecutions[executionId] = job
        job.invokeOnCompletion {
            runningExecutions.remove(executionId)
        }

        return executionId
    }

    /**
     * Cancel a running workflow execution.
     */
    fun cancelExecution(executionId: String): Boolean {
        val job = runningExecutions[executionId]
        return if (job != null) {
            job.cancel()
            true
        } else {
            false
        }
    }

    /**
     * Evaluate all conditions for a workflow.
     */
    private suspend fun evaluateConditions(
        conditions: List<Condition>,
        context: WorkflowContext,
        logs: MutableList<WorkflowLog>
    ): Boolean {
        if (conditions.isEmpty()) return true

        return conditions.all { condition ->
            try {
                val result = conditionEvaluator.evaluate(condition, context)

                logs.add(
                    WorkflowLog(
                        timestamp = System.currentTimeMillis(),
                        level = LogLevel.DEBUG,
                        message = "Condition ${condition.type} evaluated to $result",
                        step = 0,
                        stepType = "CONDITION"
                    )
                )

                // Apply logical operator
                when (condition.operator) {
                    LogicalOperator.NOT -> !result
                    else -> result // AND/OR handled at list level
                }
            } catch (e: Exception) {
                logs.add(
                    WorkflowLog(
                        timestamp = System.currentTimeMillis(),
                        level = LogLevel.ERROR,
                        message = "Condition evaluation failed: ${e.message}",
                        step = 0,
                        stepType = "CONDITION"
                    )
                )
                false
            }
        }
    }

    /**
     * Record workflow execution in database.
     */
    private suspend fun recordExecution(
        workflowId: String,
        executionId: String,
        status: WorkflowStatus,
        startTime: Long,
        logs: List<WorkflowLog>,
        context: WorkflowContext,
        error: String? = null
    ) {
        val endTime = System.currentTimeMillis()
        val execution = WorkflowExecution(
            id = executionId,
            workflowId = workflowId,
            status = status,
            startedAt = startTime,
            completedAt = endTime,
            duration = endTime - startTime,
            error = error,
            logs = logs,
            context = context
        )

        automationRepository.recordExecution(execution)
    }

    /**
     * Clean up on shutdown.
     */
    fun shutdown() {
        runningExecutions.values.forEach { it.cancel() }
        runningExecutions.clear()
        scope.cancel()
    }
}
