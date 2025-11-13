package com.localllm.localaichatapp.domain.repository

import com.localllm.localaichatapp.domain.model.Result
import com.localllm.localaichatapp.domain.model.automation.*
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for automation/workflow operations.
 */
interface AutomationRepository {
    /**
     * Create a new workflow.
     */
    suspend fun createWorkflow(workflow: Workflow): Result<Workflow>

    /**
     * Update an existing workflow.
     */
    suspend fun updateWorkflow(workflow: Workflow): Result<Workflow>

    /**
     * Delete a workflow.
     */
    suspend fun deleteWorkflow(workflowId: String): Result<Unit>

    /**
     * Get a workflow by ID.
     */
    suspend fun getWorkflow(workflowId: String): Result<Workflow?>

    /**
     * Get a workflow as a Flow.
     */
    fun getWorkflowFlow(workflowId: String): Flow<Result<Workflow?>>

    /**
     * Get all enabled workflows.
     */
    fun getEnabledWorkflows(): Flow<Result<List<Workflow>>>

    /**
     * Get all workflows.
     */
    fun getAllWorkflows(): Flow<Result<List<Workflow>>>

    /**
     * Get workflow templates.
     */
    fun getTemplates(): Flow<Result<List<WorkflowTemplate>>>

    /**
     * Get workflows by trigger type.
     */
    suspend fun getWorkflowsByTrigger(triggerType: TriggerType): Result<List<Workflow>>

    /**
     * Enable or disable a workflow.
     */
    suspend fun setWorkflowEnabled(workflowId: String, enabled: Boolean): Result<Unit>

    /**
     * Update workflow execution statistics.
     */
    suspend fun updateExecutionStats(workflowId: String): Result<Unit>

    /**
     * Record workflow execution.
     */
    suspend fun recordExecution(execution: WorkflowExecution): Result<Unit>

    /**
     * Get execution history for a workflow.
     */
    fun getExecutionHistory(workflowId: String, limit: Int = 50): Flow<Result<List<WorkflowExecution>>>

    /**
     * Get recent executions across all workflows.
     */
    fun getRecentExecutions(limit: Int = 100): Flow<Result<List<WorkflowExecution>>>

    /**
     * Get executions by status.
     */
    fun getExecutionsByStatus(status: WorkflowStatus): Flow<Result<List<WorkflowExecution>>>

    /**
     * Delete old execution records.
     */
    suspend fun deleteOldExecutions(olderThanDays: Int): Result<Unit>

    /**
     * Search workflows by name or description.
     */
    fun searchWorkflows(query: String): Flow<Result<List<Workflow>>>

    /**
     * Get workflows by tag.
     */
    fun getWorkflowsByTag(tag: String): Flow<Result<List<Workflow>>>

    /**
     * Get workflow statistics.
     */
    suspend fun getStatistics(): Result<AutomationStatistics>
}

/**
 * Statistics about automation system.
 */
data class AutomationStatistics(
    val totalWorkflows: Int,
    val enabledWorkflows: Int,
    val totalExecutions: Int,
    val successfulExecutions: Int,
    val failedExecutions: Int,
    val averageExecutionTime: Long
)
