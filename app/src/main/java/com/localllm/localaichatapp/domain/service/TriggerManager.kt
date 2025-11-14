package com.localllm.localaichatapp.domain.service

import com.localllm.localaichatapp.domain.model.Result
import com.localllm.localaichatapp.domain.model.automation.TriggerType
import com.localllm.localaichatapp.domain.model.automation.Workflow
import com.localllm.localaichatapp.domain.model.automation.WorkflowContext
import com.localllm.localaichatapp.domain.repository.AutomationRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages triggers and fires workflows when events occur.
 * Similar to Zapier's trigger monitoring system.
 */
@Singleton
class TriggerManager @Inject constructor(
    private val automationRepository: AutomationRepository,
    private val automationEngine: AutomationEngine
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val triggerCache = mutableMapOf<TriggerType, List<Workflow>>()
    private var isInitialized = false

    /**
     * Initialize the trigger manager by loading all enabled workflows.
     */
    suspend fun initialize() {
        if (isInitialized) return

        scope.launch {
            // Load all enabled workflows
            automationRepository.getEnabledWorkflows().collect { result ->
                when (result) {
                    is Result.Success -> {
                        // Group workflows by trigger type
                        val grouped = result.data.groupBy { it.trigger.type }
                        triggerCache.clear()
                        triggerCache.putAll(grouped)

                        android.util.Log.d(
                            "TriggerManager",
                            "Loaded ${result.data.size} workflows across ${triggerCache.size} trigger types"
                        )
                    }
                    is Result.Error -> {
                        android.util.Log.e(
                            "TriggerManager",
                            "Failed to load workflows",
                            result.exception
                        )
                    }
                }
            }
        }

        isInitialized = true
    }

    /**
     * Fire a trigger, executing all workflows that listen to it.
     */
    suspend fun fireTrigger(
        triggerType: TriggerType,
        context: WorkflowContext
    ) {
        val workflows = triggerCache[triggerType] ?: emptyList()

        if (workflows.isEmpty()) {
            android.util.Log.d("TriggerManager", "No workflows for trigger: $triggerType")
            return
        }

        android.util.Log.d(
            "TriggerManager",
            "Firing trigger $triggerType for ${workflows.size} workflows"
        )

        // Execute workflows in parallel
        workflows.forEach { workflow ->
            scope.launch {
                try {
                    val result = automationEngine.executeWorkflow(workflow, context)
                    android.util.Log.d(
                        "TriggerManager",
                        "Workflow ${workflow.name} completed: $result"
                    )
                } catch (e: Exception) {
                    android.util.Log.e(
                        "TriggerManager",
                        "Workflow ${workflow.name} failed",
                        e
                    )
                }
            }
        }
    }

    /**
     * Fire a trigger asynchronously (fire and forget).
     */
    fun fireTriggerAsync(
        triggerType: TriggerType,
        context: WorkflowContext
    ) {
        scope.launch {
            fireTrigger(triggerType, context)
        }
    }

    /**
     * Reload workflows (call after adding/updating/deleting workflows).
     */
    suspend fun reload() {
        val result = automationRepository.getEnabledWorkflows().firstOrNull()

        when (result) {
            is Result.Success -> {
                val grouped = result.data.groupBy { it.trigger.type }
                triggerCache.clear()
                triggerCache.putAll(grouped)

                android.util.Log.d(
                    "TriggerManager",
                    "Reloaded ${result.data.size} workflows"
                )
            }
            is Result.Error -> {
                android.util.Log.e(
                    "TriggerManager",
                    "Failed to reload workflows",
                    result.exception
                )
            }
            null -> {
                android.util.Log.w("TriggerManager", "No workflows loaded")
            }
        }
    }

    /**
     * Get count of workflows for a trigger type.
     */
    fun getWorkflowCount(triggerType: TriggerType): Int {
        return triggerCache[triggerType]?.size ?: 0
    }

    /**
     * Get all registered trigger types.
     */
    fun getRegisteredTriggers(): Set<TriggerType> {
        return triggerCache.keys
    }

    /**
     * Clear cache and shutdown.
     */
    fun shutdown() {
        triggerCache.clear()
        scope.cancel()
        isInitialized = false
    }
}

/**
 * Helper extension to easily fire triggers from anywhere in the app.
 */
suspend fun TriggerManager.fireMessageSent(
    sessionId: String,
    messageContent: String
) {
    fireTrigger(
        TriggerType.MESSAGE_SENT,
        WorkflowContext(
            triggerId = "msg_sent_${System.currentTimeMillis()}",
            triggerType = TriggerType.MESSAGE_SENT,
            sessionId = sessionId,
            data = mapOf(
                "messageContent" to messageContent,
                "sessionId" to sessionId
            )
        )
    )
}

suspend fun TriggerManager.fireSessionCreated(sessionId: String, modelId: String) {
    fireTrigger(
        TriggerType.SESSION_CREATED,
        WorkflowContext(
            triggerId = "session_created_$sessionId",
            triggerType = TriggerType.SESSION_CREATED,
            sessionId = sessionId,
            data = mapOf(
                "sessionId" to sessionId,
                "modelId" to modelId
            )
        )
    )
}

suspend fun TriggerManager.fireModelDownloaded(modelId: String) {
    fireTrigger(
        TriggerType.MODEL_DOWNLOADED,
        WorkflowContext(
            triggerId = "model_downloaded_$modelId",
            triggerType = TriggerType.MODEL_DOWNLOADED,
            data = mapOf(
                "modelId" to modelId
            )
        )
    )
}

suspend fun TriggerManager.fireAppLaunched() {
    fireTrigger(
        TriggerType.APP_LAUNCHED,
        WorkflowContext(
            triggerId = "app_launched_${System.currentTimeMillis()}",
            triggerType = TriggerType.APP_LAUNCHED
        )
    )
}
