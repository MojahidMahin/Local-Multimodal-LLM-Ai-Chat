package com.localllm.localaichatapp.data.local.database.dao

import androidx.room.*
import com.localllm.localaichatapp.data.local.database.entity.WorkflowEntity
import com.localllm.localaichatapp.data.local.database.entity.WorkflowExecutionEntity
import com.localllm.localaichatapp.data.local.database.entity.WorkflowStepEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for workflow database operations.
 */
@Dao
interface WorkflowDao {
    // Workflow operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkflow(workflow: WorkflowEntity)

    @Update
    suspend fun updateWorkflow(workflow: WorkflowEntity)

    @Delete
    suspend fun deleteWorkflow(workflow: WorkflowEntity)

    @Query("SELECT * FROM workflows WHERE id = :workflowId")
    suspend fun getWorkflowById(workflowId: String): WorkflowEntity?

    @Query("SELECT * FROM workflows WHERE id = :workflowId")
    fun getWorkflowByIdFlow(workflowId: String): Flow<WorkflowEntity?>

    @Query("SELECT * FROM workflows WHERE enabled = 1")
    fun getEnabledWorkflows(): Flow<List<WorkflowEntity>>

    @Query("SELECT * FROM workflows ORDER BY createdAt DESC")
    fun getAllWorkflows(): Flow<List<WorkflowEntity>>

    @Query("SELECT * FROM workflows WHERE isTemplate = 1")
    fun getTemplates(): Flow<List<WorkflowEntity>>

    @Query("SELECT * FROM workflows WHERE triggerType = :triggerType AND enabled = 1")
    suspend fun getWorkflowsByTrigger(triggerType: String): List<WorkflowEntity>

    @Query("UPDATE workflows SET enabled = :enabled WHERE id = :workflowId")
    suspend fun setWorkflowEnabled(workflowId: String, enabled: Boolean)

    @Query("UPDATE workflows SET lastExecutedAt = :timestamp, executionCount = executionCount + 1 WHERE id = :workflowId")
    suspend fun updateExecutionStats(workflowId: String, timestamp: Long)

    // Workflow step operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStep(step: WorkflowStepEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSteps(steps: List<WorkflowStepEntity>)

    @Query("SELECT * FROM workflow_steps WHERE workflowId = :workflowId ORDER BY `order` ASC")
    suspend fun getStepsForWorkflow(workflowId: String): List<WorkflowStepEntity>

    @Query("DELETE FROM workflow_steps WHERE workflowId = :workflowId")
    suspend fun deleteStepsForWorkflow(workflowId: String)

    // Workflow execution operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExecution(execution: WorkflowExecutionEntity)

    @Query("SELECT * FROM workflow_executions WHERE id = :executionId")
    suspend fun getExecutionById(executionId: String): WorkflowExecutionEntity?

    @Query("SELECT * FROM workflow_executions WHERE workflowId = :workflowId ORDER BY startedAt DESC LIMIT :limit")
    fun getExecutionsForWorkflow(workflowId: String, limit: Int = 50): Flow<List<WorkflowExecutionEntity>>

    @Query("SELECT * FROM workflow_executions ORDER BY startedAt DESC LIMIT :limit")
    fun getRecentExecutions(limit: Int = 100): Flow<List<WorkflowExecutionEntity>>

    @Query("SELECT * FROM workflow_executions WHERE status = :status ORDER BY startedAt DESC")
    fun getExecutionsByStatus(status: String): Flow<List<WorkflowExecutionEntity>>

    @Query("DELETE FROM workflow_executions WHERE startedAt < :timestamp")
    suspend fun deleteExecutionsOlderThan(timestamp: Long)

    @Query("DELETE FROM workflow_executions WHERE workflowId = :workflowId")
    suspend fun deleteExecutionsForWorkflow(workflowId: String)

    @Query("SELECT COUNT(*) FROM workflow_executions WHERE workflowId = :workflowId")
    suspend fun getExecutionCount(workflowId: String): Int

    @Query("SELECT COUNT(*) FROM workflow_executions WHERE workflowId = :workflowId AND status = 'SUCCESS'")
    suspend fun getSuccessfulExecutionCount(workflowId: String): Int

    // Search and filter
    @Query("SELECT * FROM workflows WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchWorkflows(query: String): Flow<List<WorkflowEntity>>

    @Query("SELECT * FROM workflows WHERE tags LIKE '%' || :tag || '%'")
    fun getWorkflowsByTag(tag: String): Flow<List<WorkflowEntity>>

    // Statistics
    @Query("SELECT COUNT(*) FROM workflows WHERE enabled = 1")
    suspend fun getEnabledWorkflowCount(): Int

    @Query("SELECT COUNT(*) FROM workflows")
    suspend fun getTotalWorkflowCount(): Int
}
