package com.localllm.localaichatapp.domain.model.automation

import kotlinx.serialization.Serializable

/**
 * Represents an action to be executed in a workflow.
 */
@Serializable
data class Action(
    val id: String,
    val type: ActionType,
    val config: ActionConfig,
    val order: Int = 0
)

/**
 * Base interface for action configurations.
 */
@Serializable
sealed class ActionConfig {
    /**
     * Show notification action configuration.
     */
    @Serializable
    data class ShowNotification(
        val title: String,
        val message: String,
        val priority: Int = 0, // -2 to 2 (PRIORITY_MIN to PRIORITY_MAX)
        val channelId: String = "automation"
    ) : ActionConfig()

    /**
     * Show toast action configuration.
     */
    @Serializable
    data class ShowToast(
        val message: String,
        val duration: String = "SHORT" // SHORT or LONG
    ) : ActionConfig()

    /**
     * Backup database action configuration.
     */
    @Serializable
    data class BackupDatabase(
        val location: String,
        val includeModels: Boolean = false,
        val compress: Boolean = true
    ) : ActionConfig()

    /**
     * Delete old sessions action configuration.
     */
    @Serializable
    data class DeleteOldSessions(
        val olderThanDays: Int,
        val excludeBookmarked: Boolean = true
    ) : ActionConfig()

    /**
     * Download model action configuration.
     */
    @Serializable
    data class DownloadModel(
        val modelId: String,
        val onlyIfNotExists: Boolean = true
    ) : ActionConfig()

    /**
     * Switch model action configuration.
     */
    @Serializable
    data class SwitchModel(
        val modelId: String
    ) : ActionConfig()

    /**
     * Share text action configuration.
     */
    @Serializable
    data class ShareText(
        val text: String,
        val title: String = "Share"
    ) : ActionConfig()

    /**
     * Copy to clipboard action configuration.
     */
    @Serializable
    data class CopyToClipboard(
        val text: String,
        val label: String = "Copied"
    ) : ActionConfig()

    /**
     * Save to file action configuration.
     */
    @Serializable
    data class SaveToFile(
        val content: String,
        val fileName: String,
        val directory: String,
        val format: String = "TXT" // TXT, JSON, CSV
    ) : ActionConfig()

    /**
     * Generate summary action configuration.
     */
    @Serializable
    data class GenerateSummary(
        val sessionId: String,
        val maxLength: Int = 500
    ) : ActionConfig()

    /**
     * Vibrate device action configuration.
     */
    @Serializable
    data class VibrateDevice(
        val durationMs: Long = 200,
        val pattern: List<Long>? = null
    ) : ActionConfig()

    /**
     * Open screen action configuration.
     */
    @Serializable
    data class OpenScreen(
        val screenRoute: String,
        val parameters: Map<String, String> = emptyMap()
    ) : ActionConfig()

    /**
     * Set setting action configuration.
     */
    @Serializable
    data class SetSetting(
        val settingKey: String,
        val settingValue: String
    ) : ActionConfig()

    /**
     * Log event action configuration.
     */
    @Serializable
    data class LogEvent(
        val eventName: String,
        val parameters: Map<String, String> = emptyMap()
    ) : ActionConfig()

    /**
     * Export data action configuration.
     */
    @Serializable
    data class ExportData(
        val dataType: String, // CONVERSATIONS, MODELS, SETTINGS, ALL
        val format: String = "JSON",
        val destination: String
    ) : ActionConfig()

    /**
     * Run workflow action configuration.
     */
    @Serializable
    data class RunWorkflow(
        val workflowId: String,
        val passContext: Boolean = true
    ) : ActionConfig()

    /**
     * Webhook action configuration.
     */
    @Serializable
    data class Webhook(
        val url: String,
        val method: String = "POST",
        val headers: Map<String, String> = emptyMap(),
        val body: String
    ) : ActionConfig()

    /**
     * No configuration needed.
     */
    @Serializable
    object Empty : ActionConfig()
}
