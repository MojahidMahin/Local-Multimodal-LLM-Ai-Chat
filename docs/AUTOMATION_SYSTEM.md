# Automation System Architecture

This document describes the Zapier-inspired automation system for LocalAiChatApp.

## Overview

The automation system enables users to create workflows that automatically respond to events within the app. Similar to Zapier, it connects triggers (events) to actions (tasks) to automate repetitive workflows.

## Architecture

```
┌─────────────────────────────────────────────────────────┐
│                   Automation System                      │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  ┌──────────┐      ┌──────────┐      ┌──────────┐     │
│  │ Triggers │─────▶│ Workflow │─────▶│ Actions  │     │
│  │ (Events) │      │  Engine  │      │ (Tasks)  │     │
│  └──────────┘      └──────────┘      └──────────┘     │
│       │                  │                  │           │
│       │                  │                  │           │
│       ▼                  ▼                  ▼           │
│  ┌──────────┐      ┌──────────┐      ┌──────────┐     │
│  │ Trigger  │      │Condition │      │ Action   │     │
│  │Listeners │      │Evaluator │      │ Handlers │     │
│  └──────────┘      └──────────┘      └──────────┘     │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

## Core Components

### 1. Triggers (Events)

Triggers are events that initiate workflows:

**App Triggers**:
- `MESSAGE_SENT` - User sends a message
- `MESSAGE_RECEIVED` - AI responds
- `SESSION_CREATED` - New chat session created
- `SESSION_DELETED` - Session deleted
- `IMAGE_UPLOADED` - Image added to chat
- `AUDIO_UPLOADED` - Audio added to chat

**Model Triggers**:
- `MODEL_DOWNLOADED` - Model download completed
- `MODEL_DELETED` - Model removed
- `MODEL_SWITCHED` - Active model changed

**System Triggers**:
- `APP_LAUNCHED` - App starts
- `APP_BACKGROUNDED` - App goes to background
- `LOW_STORAGE` - Storage space low
- `SCHEDULED` - Time-based trigger

**Performance Triggers**:
- `HIGH_MEMORY_USAGE` - Memory threshold exceeded
- `SLOW_RESPONSE` - Response time exceeded
- `BENCHMARK_COMPLETED` - Performance test done

### 2. Actions (Tasks)

Actions are tasks executed when triggers fire:

**Notification Actions**:
- `SHOW_NOTIFICATION` - Display Android notification
- `SHOW_TOAST` - Show toast message
- `VIBRATE_DEVICE` - Trigger device vibration

**Data Actions**:
- `SAVE_CONVERSATION` - Export conversation
- `BACKUP_DATABASE` - Backup all data
- `DELETE_OLD_SESSIONS` - Clean up old chats
- `ARCHIVE_SESSION` - Archive conversation

**Model Actions**:
- `DOWNLOAD_MODEL` - Start model download
- `SWITCH_MODEL` - Change active model
- `OPTIMIZE_MODEL` - Run model optimization

**Integration Actions**:
- `SHARE_TEXT` - Share via Android share sheet
- `COPY_TO_CLIPBOARD` - Copy text
- `SAVE_TO_FILE` - Export to file
- `SEND_TO_APP` - Send to external app

**AI Actions**:
- `GENERATE_SUMMARY` - Summarize conversation
- `ANALYZE_SENTIMENT` - Analyze message sentiment
- `EXTRACT_KEYWORDS` - Extract key topics

### 3. Conditions

Conditions control workflow execution:

**Comparison Conditions**:
- `EQUALS`, `NOT_EQUALS`
- `CONTAINS`, `NOT_CONTAINS`
- `STARTS_WITH`, `ENDS_WITH`
- `GREATER_THAN`, `LESS_THAN`

**Logical Conditions**:
- `AND` - All conditions must be true
- `OR` - Any condition must be true
- `NOT` - Negates condition

**Context Conditions**:
- `TIME_OF_DAY` - Check current time
- `DAY_OF_WEEK` - Check day
- `BATTERY_LEVEL` - Check battery
- `NETWORK_AVAILABLE` - Check connectivity

### 4. Workflows

Workflows chain triggers, conditions, and actions:

```kotlin
Workflow {
    id = "auto-backup"
    name = "Daily Backup"
    enabled = true

    trigger = Trigger.SCHEDULED {
        time = "03:00" // 3 AM daily
    }

    conditions = listOf(
        Condition.BATTERY_LEVEL { min = 20 }
    )

    actions = listOf(
        Action.BACKUP_DATABASE {
            location = "/sdcard/backups/"
        },
        Action.SHOW_NOTIFICATION {
            title = "Backup Complete"
            message = "Data backed up successfully"
        }
    )
}
```

## Database Schema

### Workflow Entity
```kotlin
@Entity(tableName = "workflows")
data class WorkflowEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String?,
    val enabled: Boolean,
    val triggerType: String,
    val triggerConfig: String, // JSON
    val createdAt: Long,
    val updatedAt: Long
)
```

### WorkflowStep Entity
```kotlin
@Entity(tableName = "workflow_steps")
data class WorkflowStepEntity(
    @PrimaryKey val id: String,
    val workflowId: String,
    val order: Int,
    val type: String, // ACTION or CONDITION
    val actionType: String?,
    val conditionType: String?,
    val config: String, // JSON
    @ForeignKey(
        entity = WorkflowEntity::class,
        parentColumns = ["id"],
        childColumns = ["workflowId"],
        onDelete = CASCADE
    )
)
```

### WorkflowExecution Entity
```kotlin
@Entity(tableName = "workflow_executions")
data class WorkflowExecutionEntity(
    @PrimaryKey val id: String,
    val workflowId: String,
    val status: String, // SUCCESS, FAILED, RUNNING
    val startedAt: Long,
    val completedAt: Long?,
    val error: String?,
    val logs: String // JSON array of log entries
)
```

## Implementation Details

### Automation Engine

```kotlin
class AutomationEngine @Inject constructor(
    private val workflowRepository: WorkflowRepository,
    private val triggerManager: TriggerManager,
    private val actionExecutor: ActionExecutor,
    private val conditionEvaluator: ConditionEvaluator
) {
    suspend fun executeWorkflow(
        workflowId: String,
        context: WorkflowContext
    ): WorkflowResult {
        // 1. Load workflow
        val workflow = workflowRepository.getWorkflow(workflowId)

        // 2. Evaluate conditions
        if (!conditionEvaluator.evaluate(workflow.conditions, context)) {
            return WorkflowResult.Skipped
        }

        // 3. Execute actions in sequence
        workflow.actions.forEach { action ->
            actionExecutor.execute(action, context)
        }

        return WorkflowResult.Success
    }
}
```

### Trigger Manager

```kotlin
class TriggerManager @Inject constructor(
    private val workflowRepository: WorkflowRepository,
    private val automationEngine: AutomationEngine
) {
    private val listeners = mutableMapOf<TriggerType, MutableList<WorkflowId>>()

    fun registerTrigger(triggerType: TriggerType, workflowId: String) {
        listeners.getOrPut(triggerType) { mutableListOf() }
            .add(workflowId)
    }

    suspend fun fireTrigger(
        triggerType: TriggerType,
        context: WorkflowContext
    ) {
        listeners[triggerType]?.forEach { workflowId ->
            automationEngine.executeWorkflow(workflowId, context)
        }
    }
}
```

### Action Executor

```kotlin
class ActionExecutor @Inject constructor(
    private val context: Context,
    private val chatRepository: ChatRepository,
    private val modelRepository: ModelRepository,
    private val notificationManager: NotificationManager
) {
    suspend fun execute(action: Action, context: WorkflowContext) {
        when (action) {
            is Action.ShowNotification -> showNotification(action)
            is Action.BackupDatabase -> backupDatabase(action)
            is Action.DownloadModel -> downloadModel(action)
            is Action.ShareText -> shareText(action)
            // ... more actions
        }
    }
}
```

## Use Cases

### Example 1: Auto-Backup on Schedule
```kotlin
Workflow(
    name = "Nightly Backup",
    trigger = Trigger.Scheduled("03:00"),
    conditions = listOf(
        Condition.BatteryLevel(min = 20),
        Condition.StorageAvailable(min = 100_MB)
    ),
    actions = listOf(
        Action.BackupDatabase(),
        Action.ShowNotification("Backup Complete")
    )
)
```

### Example 2: Smart Model Switching
```kotlin
Workflow(
    name = "Battery Saver",
    trigger = Trigger.BatteryLow(threshold = 20),
    actions = listOf(
        Action.SwitchModel("gemma-2b"), // Smaller model
        Action.ShowToast("Switched to power-saving model")
    )
)
```

### Example 3: Conversation Summarizer
```kotlin
Workflow(
    name = "Auto Summarize",
    trigger = Trigger.MessageCount(threshold = 50),
    conditions = listOf(
        Condition.SessionType(TaskType.CHAT)
    ),
    actions = listOf(
        Action.GenerateSummary(),
        Action.SaveToFile(),
        Action.ShowNotification("Summary Generated")
    )
)
```

### Example 4: Content Moderation
```kotlin
Workflow(
    name = "Content Filter",
    trigger = Trigger.MessageSent,
    conditions = listOf(
        Condition.Contains(keywords = listOf("spam", "abuse"))
    ),
    actions = listOf(
        Action.ShowWarning(),
        Action.LogIncident()
    )
)
```

## User Interface

### Automation Hub
- List all workflows
- Enable/disable workflows
- View execution history
- Create new workflows

### Workflow Builder
- **Trigger Selection**: Choose event type
- **Condition Builder**: Add logic conditions
- **Action Builder**: Chain multiple actions
- **Test Mode**: Preview workflow

### Templates
Pre-built workflows users can enable:
1. Daily Backup
2. Battery Saver
3. Storage Manager
4. Auto-Archive Old Chats
5. Performance Monitor
6. Smart Notifications

## Benefits

1. **Automation**: Reduce repetitive tasks
2. **Customization**: Users control app behavior
3. **Efficiency**: Optimize resource usage
4. **Flexibility**: Adapt to user patterns
5. **Power User Features**: Advanced capabilities

## Future Enhancements

1. **Visual Workflow Builder**: Drag-and-drop UI
2. **Webhook Support**: External integrations
3. **Custom Scripts**: User-defined actions
4. **Workflow Marketplace**: Share templates
5. **Machine Learning**: Suggest automations
6. **Cross-Device Sync**: Share workflows (optional)

## Security Considerations

1. **Permission Checks**: Verify app permissions
2. **Rate Limiting**: Prevent excessive executions
3. **Validation**: Sanitize user inputs
4. **Logging**: Track all executions
5. **User Consent**: Require approval for sensitive actions

---

This automation system brings Zapier-like power to the local AI chat app, enabling sophisticated workflows while maintaining privacy and offline functionality.
