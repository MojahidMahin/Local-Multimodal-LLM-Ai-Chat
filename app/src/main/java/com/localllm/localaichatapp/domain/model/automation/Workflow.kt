package com.localllm.localaichatapp.domain.model.automation

/**
 * Represents a complete workflow (Zap in Zapier terminology).
 * A workflow connects a trigger to a series of conditions and actions.
 */
data class Workflow(
    val id: String,
    val name: String,
    val description: String?,
    val enabled: Boolean,
    val trigger: Trigger,
    val conditions: List<Condition> = emptyList(),
    val actions: List<Action>,
    val createdAt: Long,
    val updatedAt: Long,
    val lastExecutedAt: Long? = null,
    val executionCount: Int = 0,
    val tags: List<String> = emptyList(),
    val isTemplate: Boolean = false
)

/**
 * Context passed during workflow execution.
 * Contains runtime information available to conditions and actions.
 */
data class WorkflowContext(
    val triggerId: String,
    val triggerType: TriggerType,
    val timestamp: Long = System.currentTimeMillis(),
    val data: Map<String, Any> = emptyMap(),
    val userId: String? = null,
    val sessionId: String? = null
) {
    /**
     * Get a value from context data.
     */
    inline fun <reified T> get(key: String): T? {
        return data[key] as? T
    }

    /**
     * Check if a key exists in context data.
     */
    fun has(key: String): Boolean {
        return data.containsKey(key)
    }

    /**
     * Create a new context with additional data.
     */
    fun withData(vararg pairs: Pair<String, Any>): WorkflowContext {
        return copy(data = data + pairs)
    }
}

/**
 * Result of workflow execution.
 */
sealed class WorkflowResult {
    data class Success(
        val workflowId: String,
        val executionId: String,
        val actionsExecuted: Int,
        val duration: Long,
        val logs: List<String> = emptyList()
    ) : WorkflowResult()

    data class Skipped(
        val workflowId: String,
        val reason: String
    ) : WorkflowResult()

    data class Failed(
        val workflowId: String,
        val error: Exception,
        val failedAtStep: Int,
        val partialLogs: List<String> = emptyList()
    ) : WorkflowResult()

    data class Cancelled(
        val workflowId: String,
        val reason: String
    ) : WorkflowResult()
}

/**
 * Execution log entry.
 */
data class WorkflowLog(
    val timestamp: Long,
    val level: LogLevel,
    val message: String,
    val step: Int,
    val stepType: String, // TRIGGER, CONDITION, ACTION
    val details: Map<String, String> = emptyMap()
)

enum class LogLevel {
    DEBUG,
    INFO,
    WARNING,
    ERROR
}

/**
 * Workflow execution status.
 */
enum class WorkflowStatus {
    PENDING,
    RUNNING,
    SUCCESS,
    FAILED,
    SKIPPED,
    CANCELLED
}

/**
 * Workflow template for common use cases.
 */
data class WorkflowTemplate(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val icon: String,
    val workflow: Workflow,
    val popularity: Int = 0
)

/**
 * Workflow execution history entry.
 */
data class WorkflowExecution(
    val id: String,
    val workflowId: String,
    val status: WorkflowStatus,
    val startedAt: Long,
    val completedAt: Long?,
    val duration: Long?,
    val error: String?,
    val logs: List<WorkflowLog>,
    val context: WorkflowContext
)
