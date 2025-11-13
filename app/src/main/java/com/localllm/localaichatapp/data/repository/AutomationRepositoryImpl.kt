package com.localllm.localaichatapp.data.repository

import com.localllm.localaichatapp.data.local.database.dao.WorkflowDao
import com.localllm.localaichatapp.data.mapper.WorkflowMapper
import com.localllm.localaichatapp.domain.model.Result
import com.localllm.localaichatapp.domain.model.automation.*
import com.localllm.localaichatapp.domain.repository.AutomationRepository
import com.localllm.localaichatapp.domain.repository.AutomationStatistics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of AutomationRepository.
 */
class AutomationRepositoryImpl @Inject constructor(
    private val workflowDao: WorkflowDao,
    private val mapper: WorkflowMapper
) : AutomationRepository {

    override suspend fun createWorkflow(workflow: Workflow): Result<Workflow> = try {
        val entity = mapper.toEntity(workflow)
        workflowDao.insertWorkflow(entity)

        // Insert workflow steps
        val steps = mapper.toStepEntities(workflow)
        workflowDao.insertSteps(steps)

        Result.Success(workflow)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun updateWorkflow(workflow: Workflow): Result<Workflow> = try {
        val entity = mapper.toEntity(workflow)
        workflowDao.updateWorkflow(entity)

        // Delete old steps and insert new ones
        workflowDao.deleteStepsForWorkflow(workflow.id)
        val steps = mapper.toStepEntities(workflow)
        workflowDao.insertSteps(steps)

        Result.Success(workflow)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun deleteWorkflow(workflowId: String): Result<Unit> = try {
        val workflow = workflowDao.getWorkflowById(workflowId)
        if (workflow != null) {
            workflowDao.deleteWorkflow(workflow)
            Result.Success(Unit)
        } else {
            Result.Error(Exception("Workflow not found"))
        }
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun getWorkflow(workflowId: String): Result<Workflow?> = try {
        val entity = workflowDao.getWorkflowById(workflowId)
        if (entity != null) {
            val steps = workflowDao.getStepsForWorkflow(workflowId)
            val workflow = mapper.toDomain(entity, steps)
            Result.Success(workflow)
        } else {
            Result.Success(null)
        }
    } catch (e: Exception) {
        Result.Error(e)
    }

    override fun getWorkflowFlow(workflowId: String): Flow<Result<Workflow?>> {
        return workflowDao.getWorkflowByIdFlow(workflowId)
            .map { entity ->
                if (entity != null) {
                    val steps = workflowDao.getStepsForWorkflow(workflowId)
                    val workflow = mapper.toDomain(entity, steps)
                    Result.Success(workflow)
                } else {
                    Result.Success(null)
                }
            }
            .catch { e -> emit(Result.Error(Exception(e))) }
    }

    override fun getEnabledWorkflows(): Flow<Result<List<Workflow>>> {
        return workflowDao.getEnabledWorkflows()
            .map { entities ->
                val workflows = entities.map { entity ->
                    val steps = workflowDao.getStepsForWorkflow(entity.id)
                    mapper.toDomain(entity, steps)
                }
                Result.Success(workflows)
            }
            .catch { e -> emit(Result.Error(Exception(e))) }
    }

    override fun getAllWorkflows(): Flow<Result<List<Workflow>>> {
        return workflowDao.getAllWorkflows()
            .map { entities ->
                val workflows = entities.map { entity ->
                    val steps = workflowDao.getStepsForWorkflow(entity.id)
                    mapper.toDomain(entity, steps)
                }
                Result.Success(workflows)
            }
            .catch { e -> emit(Result.Error(Exception(e))) }
    }

    override fun getTemplates(): Flow<Result<List<WorkflowTemplate>>> {
        return workflowDao.getTemplates()
            .map { entities ->
                val templates = entities.map { entity ->
                    val steps = workflowDao.getStepsForWorkflow(entity.id)
                    val workflow = mapper.toDomain(entity, steps)
                    WorkflowTemplate(
                        id = workflow.id,
                        name = workflow.name,
                        description = workflow.description ?: "",
                        category = "General",
                        icon = "automation",
                        workflow = workflow,
                        popularity = workflow.executionCount
                    )
                }
                Result.Success(templates)
            }
            .catch { e -> emit(Result.Error(Exception(e))) }
    }

    override suspend fun getWorkflowsByTrigger(triggerType: TriggerType): Result<List<Workflow>> = try {
        val entities = workflowDao.getWorkflowsByTrigger(triggerType.name)
        val workflows = entities.map { entity ->
            val steps = workflowDao.getStepsForWorkflow(entity.id)
            mapper.toDomain(entity, steps)
        }
        Result.Success(workflows)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun setWorkflowEnabled(workflowId: String, enabled: Boolean): Result<Unit> = try {
        workflowDao.setWorkflowEnabled(workflowId, enabled)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun updateExecutionStats(workflowId: String): Result<Unit> = try {
        workflowDao.updateExecutionStats(workflowId, System.currentTimeMillis())
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun recordExecution(execution: WorkflowExecution): Result<Unit> = try {
        val entity = mapper.toExecutionEntity(execution)
        workflowDao.insertExecution(entity)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override fun getExecutionHistory(workflowId: String, limit: Int): Flow<Result<List<WorkflowExecution>>> {
        return workflowDao.getExecutionsForWorkflow(workflowId, limit)
            .map { entities ->
                val executions = entities.map { mapper.toExecutionDomain(it) }
                Result.Success(executions)
            }
            .catch { e -> emit(Result.Error(Exception(e))) }
    }

    override fun getRecentExecutions(limit: Int): Flow<Result<List<WorkflowExecution>>> {
        return workflowDao.getRecentExecutions(limit)
            .map { entities ->
                val executions = entities.map { mapper.toExecutionDomain(it) }
                Result.Success(executions)
            }
            .catch { e -> emit(Result.Error(Exception(e))) }
    }

    override fun getExecutionsByStatus(status: WorkflowStatus): Flow<Result<List<WorkflowExecution>>> {
        return workflowDao.getExecutionsByStatus(status.name)
            .map { entities ->
                val executions = entities.map { mapper.toExecutionDomain(it) }
                Result.Success(executions)
            }
            .catch { e -> emit(Result.Error(Exception(e))) }
    }

    override suspend fun deleteOldExecutions(olderThanDays: Int): Result<Unit> = try {
        val cutoffTime = System.currentTimeMillis() - (olderThanDays * 24 * 60 * 60 * 1000L)
        workflowDao.deleteExecutionsOlderThan(cutoffTime)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override fun searchWorkflows(query: String): Flow<Result<List<Workflow>>> {
        return workflowDao.searchWorkflows(query)
            .map { entities ->
                val workflows = entities.map { entity ->
                    val steps = workflowDao.getStepsForWorkflow(entity.id)
                    mapper.toDomain(entity, steps)
                }
                Result.Success(workflows)
            }
            .catch { e -> emit(Result.Error(Exception(e))) }
    }

    override fun getWorkflowsByTag(tag: String): Flow<Result<List<Workflow>>> {
        return workflowDao.getWorkflowsByTag(tag)
            .map { entities ->
                val workflows = entities.map { entity ->
                    val steps = workflowDao.getStepsForWorkflow(entity.id)
                    mapper.toDomain(entity, steps)
                }
                Result.Success(workflows)
            }
            .catch { e -> emit(Result.Error(Exception(e))) }
    }

    override suspend fun getStatistics(): Result<AutomationStatistics> = try {
        val totalWorkflows = workflowDao.getTotalWorkflowCount()
        val enabledWorkflows = workflowDao.getEnabledWorkflowCount()

        // For simplicity, these would need additional queries
        val stats = AutomationStatistics(
            totalWorkflows = totalWorkflows,
            enabledWorkflows = enabledWorkflows,
            totalExecutions = 0, // Would need aggregation query
            successfulExecutions = 0, // Would need aggregation query
            failedExecutions = 0, // Would need aggregation query
            averageExecutionTime = 0L // Would need aggregation query
        )
        Result.Success(stats)
    } catch (e: Exception) {
        Result.Error(e)
    }
}
