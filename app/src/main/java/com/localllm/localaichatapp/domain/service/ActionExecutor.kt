package com.localllm.localaichatapp.domain.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.localllm.localaichatapp.R
import com.localllm.localaichatapp.domain.model.automation.Action
import com.localllm.localaichatapp.domain.model.automation.ActionConfig
import com.localllm.localaichatapp.domain.model.automation.ActionType
import com.localllm.localaichatapp.domain.model.automation.WorkflowContext
import com.localllm.localaichatapp.domain.repository.ChatRepository
import com.localllm.localaichatapp.domain.repository.ModelRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Executes actions in workflows.
 * Handles all action types defined in the automation system.
 */
@Singleton
class ActionExecutor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val chatRepository: ChatRepository,
    private val modelRepository: ModelRepository
) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    /**
     * Execute an action with the given context.
     */
    suspend fun execute(action: Action, workflowContext: WorkflowContext) {
        when (action.type) {
            ActionType.SHOW_NOTIFICATION -> executeShowNotification(action.config, workflowContext)
            ActionType.SHOW_TOAST -> executeShowToast(action.config, workflowContext)
            ActionType.VIBRATE_DEVICE -> executeVibrateDevice(action.config)
            ActionType.COPY_TO_CLIPBOARD -> executeCopyToClipboard(action.config, workflowContext)
            ActionType.SHARE_TEXT -> executeShareText(action.config, workflowContext)
            ActionType.SAVE_TO_FILE -> executeSaveToFile(action.config, workflowContext)
            ActionType.BACKUP_DATABASE -> executeBackupDatabase(action.config)
            ActionType.DELETE_OLD_SESSIONS -> executeDeleteOldSessions(action.config)
            ActionType.DOWNLOAD_MODEL -> executeDownloadModel(action.config)
            ActionType.SWITCH_MODEL -> executeSwitchModel(action.config)
            ActionType.LOG_EVENT -> executeLogEvent(action.config, workflowContext)
            else -> {
                // Actions not yet implemented - log for now
                android.util.Log.d("ActionExecutor", "Action ${action.type} not yet implemented")
            }
        }
    }

    private suspend fun executeShowNotification(config: ActionConfig, context: WorkflowContext) {
        withContext(Dispatchers.Main) {
            when (config) {
                is ActionConfig.ShowNotification -> {
                    val title = resolveVariables(config.title, context)
                    val message = resolveVariables(config.message, context)

                    val notification = NotificationCompat.Builder(this@ActionExecutor.context, config.channelId)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setSmallIcon(R.drawable.ic_launcher_foreground) // Update with actual icon
                        .setPriority(config.priority)
                        .setAutoCancel(true)
                        .build()

                    val notificationId = System.currentTimeMillis().toInt()
                    notificationManager.notify(notificationId, notification)
                }
                else -> {}
            }
        }
    }

    private suspend fun executeShowToast(config: ActionConfig, context: WorkflowContext) {
        withContext(Dispatchers.Main) {
            when (config) {
                is ActionConfig.ShowToast -> {
                    val message = resolveVariables(config.message, context)
                    val duration = if (config.duration == "LONG") {
                        Toast.LENGTH_LONG
                    } else {
                        Toast.LENGTH_SHORT
                    }
                    Toast.makeText(this@ActionExecutor.context, message, duration).show()
                }
                else -> {}
            }
        }
    }

    private fun executeVibrateDevice(config: ActionConfig) {
        when (config) {
            is ActionConfig.VibrateDevice -> {
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.let {
                    if (it.hasVibrator()) {
                        if (config.pattern != null) {
                            val effect = VibrationEffect.createWaveform(config.pattern.toLongArray(), -1)
                            it.vibrate(effect)
                        } else {
                            val effect = VibrationEffect.createOneShot(
                                config.durationMs,
                                VibrationEffect.DEFAULT_AMPLITUDE
                            )
                            it.vibrate(effect)
                        }
                    }
                }
            }
            else -> {}
        }
    }

    private fun executeCopyToClipboard(config: ActionConfig, context: WorkflowContext) {
        when (config) {
            is ActionConfig.CopyToClipboard -> {
                val text = resolveVariables(config.text, context)
                val clipboard = this.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(config.label, text)
                clipboard.setPrimaryClip(clip)
            }
            else -> {}
        }
    }

    private fun executeShareText(config: ActionConfig, context: WorkflowContext) {
        when (config) {
            is ActionConfig.ShareText -> {
                val text = resolveVariables(config.text, context)
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, text)
                    type = "text/plain"
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                val chooser = Intent.createChooser(shareIntent, config.title).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                this.context.startActivity(chooser)
            }
            else -> {}
        }
    }

    private suspend fun executeSaveToFile(config: ActionConfig, context: WorkflowContext) {
        withContext(Dispatchers.IO) {
            when (config) {
                is ActionConfig.SaveToFile -> {
                    try {
                        val content = resolveVariables(config.content, context)
                        val directory = File(config.directory)

                        if (!directory.exists()) {
                            directory.mkdirs()
                        }

                        val file = File(directory, config.fileName)
                        file.writeText(content)
                    } catch (e: Exception) {
                        android.util.Log.e("ActionExecutor", "Failed to save file", e)
                        throw e
                    }
                }
                else -> {}
            }
        }
    }

    private suspend fun executeBackupDatabase(config: ActionConfig) {
        withContext(Dispatchers.IO) {
            when (config) {
                is ActionConfig.BackupDatabase -> {
                    try {
                        val dbPath = context.getDatabasePath("chat_database")
                        val backupDir = File(config.location)

                        if (!backupDir.exists()) {
                            backupDir.mkdirs()
                        }

                        val timestamp = System.currentTimeMillis()
                        val backupFile = File(backupDir, "chat_backup_$timestamp.db")

                        dbPath.copyTo(backupFile, overwrite = true)

                        android.util.Log.d("ActionExecutor", "Database backed up to ${backupFile.absolutePath}")
                    } catch (e: Exception) {
                        android.util.Log.e("ActionExecutor", "Failed to backup database", e)
                        throw e
                    }
                }
                else -> {}
            }
        }
    }

    private suspend fun executeDeleteOldSessions(config: ActionConfig) {
        when (config) {
            is ActionConfig.DeleteOldSessions -> {
                try {
                    val cutoffTime = System.currentTimeMillis() - (config.olderThanDays * 24 * 60 * 60 * 1000L)
                    // This would need implementation in ChatRepository
                    // chatRepository.deleteSessionsOlderThan(cutoffTime, config.excludeBookmarked)
                    android.util.Log.d("ActionExecutor", "Delete old sessions: cutoff=$cutoffTime")
                } catch (e: Exception) {
                    android.util.Log.e("ActionExecutor", "Failed to delete old sessions", e)
                    throw e
                }
            }
            else -> {}
        }
    }

    private suspend fun executeDownloadModel(config: ActionConfig) {
        when (config) {
            is ActionConfig.DownloadModel -> {
                try {
                    // This would trigger model download
                    android.util.Log.d("ActionExecutor", "Download model: ${config.modelId}")
                } catch (e: Exception) {
                    android.util.Log.e("ActionExecutor", "Failed to download model", e)
                    throw e
                }
            }
            else -> {}
        }
    }

    private suspend fun executeSwitchModel(config: ActionConfig) {
        when (config) {
            is ActionConfig.SwitchModel -> {
                try {
                    // This would switch active model
                    android.util.Log.d("ActionExecutor", "Switch model: ${config.modelId}")
                } catch (e: Exception) {
                    android.util.Log.e("ActionExecutor", "Failed to switch model", e)
                    throw e
                }
            }
            else -> {}
        }
    }

    private fun executeLogEvent(config: ActionConfig, context: WorkflowContext) {
        when (config) {
            is ActionConfig.LogEvent -> {
                val params = config.parameters.map { (k, v) ->
                    "$k=${resolveVariables(v, context)}"
                }.joinToString(", ")
                android.util.Log.i("AutomationEvent", "${config.eventName}: $params")
            }
            else -> {}
        }
    }

    /**
     * Resolve variables in strings (e.g., ${variableName}).
     */
    private fun resolveVariables(text: String, context: WorkflowContext): String {
        var result = text
        val regex = Regex("\\$\\{([^}]+)\\}")

        regex.findAll(text).forEach { match ->
            val variableName = match.groupValues[1]
            val value = context.get<Any>(variableName)?.toString() ?: ""
            result = result.replace(match.value, value)
        }

        return result
    }

    /**
     * Create notification channel for automation notifications.
     */
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "automation",
            "Automation",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifications from automation workflows"
        }

        notificationManager.createNotificationChannel(channel)
    }
}
