package com.localllm.localaichatapp.data.mapper

import com.localllm.localaichatapp.data.local.database.entity.WorkflowEntity
import com.localllm.localaichatapp.data.local.database.entity.WorkflowExecutionEntity
import com.localllm.localaichatapp.data.local.database.entity.WorkflowStepEntity
import com.localllm.localaichatapp.domain.model.automation.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * Mapper for converting between Workflow domain models and database entities.
 */
class WorkflowMapper @Inject constructor() {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
    }

    /**
     * Convert Workflow domain model to WorkflowEntity.
     */
    fun toEntity(workflow: Workflow): WorkflowEntity {
        return WorkflowEntity(
            id = workflow.id,
            name = workflow.name,
            description = workflow.description,
            enabled = workflow.enabled,
            triggerType = workflow.trigger.type.name,
            triggerConfig = json.encodeToString(workflow.trigger.config),
            createdAt = workflow.createdAt,
            updatedAt = workflow.updatedAt,
            lastExecutedAt = workflow.lastExecutedAt,
            executionCount = workflow.executionCount,
            tags = json.encodeToString(workflow.tags),
            isTemplate = workflow.isTemplate
        )
    }

    /**
     * Convert workflow steps to entities.
     */
    fun toStepEntities(workflow: Workflow): List<WorkflowStepEntity> {
        val steps = mutableListOf<WorkflowStepEntity>()

        // Add condition steps
        workflow.conditions.forEachIndexed { index, condition ->
            steps.add(
                WorkflowStepEntity(
                    id = "${workflow.id}_cond_$index",
                    workflowId = workflow.id,
                    order = index,
                    stepType = "CONDITION",
                    type = condition.type.name,
                    config = json.encodeToString(condition.config),
                    operator = condition.operator.name
                )
            )
        }

        // Add action steps
        workflow.actions.forEachIndexed { index, action ->
            steps.add(
                WorkflowStepEntity(
                    id = action.id,
                    workflowId = workflow.id,
                    order = workflow.conditions.size + index,
                    stepType = "ACTION",
                    type = action.type.name,
                    config = json.encodeToString(action.config),
                    operator = null
                )
            )
        }

        return steps
    }

    /**
     * Convert WorkflowEntity and steps to Workflow domain model.
     */
    fun toDomain(entity: WorkflowEntity, steps: List<WorkflowStepEntity>): Workflow {
        val triggerType = TriggerType.valueOf(entity.triggerType)
        val triggerConfig = parseTriggerConfig(entity.triggerConfig, triggerType)

        val conditions = steps
            .filter { it.stepType == "CONDITION" }
            .map { step ->
                Condition(
                    id = step.id,
                    type = ConditionType.valueOf(step.type),
                    config = parseConditionConfig(step.config, ConditionType.valueOf(step.type)),
                    operator = LogicalOperator.valueOf(step.operator ?: "AND")
                )
            }

        val actions = steps
            .filter { it.stepType == "ACTION" }
            .map { step ->
                Action(
                    id = step.id,
                    type = ActionType.valueOf(step.type),
                    config = parseActionConfig(step.config, ActionType.valueOf(step.type)),
                    order = step.order
                )
            }

        return Workflow(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            enabled = entity.enabled,
            trigger = Trigger(triggerType, triggerConfig),
            conditions = conditions,
            actions = actions,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            lastExecutedAt = entity.lastExecutedAt,
            executionCount = entity.executionCount,
            tags = json.decodeFromString<List<String>>(entity.tags),
            isTemplate = entity.isTemplate
        )
    }

    /**
     * Convert WorkflowExecution to entity.
     */
    fun toExecutionEntity(execution: WorkflowExecution): WorkflowExecutionEntity {
        return WorkflowExecutionEntity(
            id = execution.id,
            workflowId = execution.workflowId,
            status = execution.status.name,
            startedAt = execution.startedAt,
            completedAt = execution.completedAt,
            duration = execution.duration,
            error = execution.error,
            logs = json.encodeToString(execution.logs),
            context = json.encodeToString(execution.context)
        )
    }

    /**
     * Convert entity to WorkflowExecution.
     */
    fun toExecutionDomain(entity: WorkflowExecutionEntity): WorkflowExecution {
        return WorkflowExecution(
            id = entity.id,
            workflowId = entity.workflowId,
            status = WorkflowStatus.valueOf(entity.status),
            startedAt = entity.startedAt,
            completedAt = entity.completedAt,
            duration = entity.duration,
            error = entity.error,
            logs = json.decodeFromString(entity.logs),
            context = json.decodeFromString(entity.context)
        )
    }

    private fun parseTriggerConfig(configJson: String, type: TriggerType): TriggerConfig {
        return try {
            when (type) {
                TriggerType.SCHEDULED, TriggerType.TIME_BASED ->
                    json.decodeFromString<TriggerConfig.Scheduled>(configJson)
                TriggerType.LOW_STORAGE, TriggerType.STORAGE_THRESHOLD_REACHED ->
                    json.decodeFromString<TriggerConfig.StorageThreshold>(configJson)
                TriggerType.HIGH_MEMORY_USAGE, TriggerType.SLOW_RESPONSE ->
                    json.decodeFromString<TriggerConfig.PerformanceThreshold>(configJson)
                else -> TriggerConfig.Event
            }
        } catch (e: Exception) {
            TriggerConfig.Event
        }
    }

    private fun parseConditionConfig(configJson: String, type: ConditionType): ConditionConfig {
        return try {
            when (type) {
                ConditionType.CONTAINS, ConditionType.NOT_CONTAINS ->
                    json.decodeFromString<ConditionConfig.Contains>(configJson)
                ConditionType.TIME_OF_DAY ->
                    json.decodeFromString<ConditionConfig.TimeRange>(configJson)
                ConditionType.DAY_OF_WEEK ->
                    json.decodeFromString<ConditionConfig.DayOfWeek>(configJson)
                ConditionType.BATTERY_LEVEL ->
                    json.decodeFromString<ConditionConfig.BatteryLevel>(configJson)
                ConditionType.NETWORK_AVAILABLE ->
                    json.decodeFromString<ConditionConfig.NetworkAvailable>(configJson)
                ConditionType.STORAGE_AVAILABLE ->
                    json.decodeFromString<ConditionConfig.StorageAvailable>(configJson)
                ConditionType.REGEX_MATCH ->
                    json.decodeFromString<ConditionConfig.RegexMatch>(configJson)
                else ->
                    json.decodeFromString<ConditionConfig.Compare>(configJson)
            }
        } catch (e: Exception) {
            ConditionConfig.Empty
        }
    }

    private fun parseActionConfig(configJson: String, type: ActionType): ActionConfig {
        return try {
            when (type) {
                ActionType.SHOW_NOTIFICATION ->
                    json.decodeFromString<ActionConfig.ShowNotification>(configJson)
                ActionType.SHOW_TOAST ->
                    json.decodeFromString<ActionConfig.ShowToast>(configJson)
                ActionType.BACKUP_DATABASE ->
                    json.decodeFromString<ActionConfig.BackupDatabase>(configJson)
                ActionType.DELETE_OLD_SESSIONS ->
                    json.decodeFromString<ActionConfig.DeleteOldSessions>(configJson)
                ActionType.DOWNLOAD_MODEL ->
                    json.decodeFromString<ActionConfig.DownloadModel>(configJson)
                ActionType.SWITCH_MODEL ->
                    json.decodeFromString<ActionConfig.SwitchModel>(configJson)
                ActionType.SHARE_TEXT ->
                    json.decodeFromString<ActionConfig.ShareText>(configJson)
                ActionType.COPY_TO_CLIPBOARD ->
                    json.decodeFromString<ActionConfig.CopyToClipboard>(configJson)
                ActionType.SAVE_TO_FILE ->
                    json.decodeFromString<ActionConfig.SaveToFile>(configJson)
                ActionType.GENERATE_SUMMARY ->
                    json.decodeFromString<ActionConfig.GenerateSummary>(configJson)
                ActionType.VIBRATE_DEVICE ->
                    json.decodeFromString<ActionConfig.VibrateDevice>(configJson)
                ActionType.OPEN_SCREEN ->
                    json.decodeFromString<ActionConfig.OpenScreen>(configJson)
                ActionType.SET_SETTING ->
                    json.decodeFromString<ActionConfig.SetSetting>(configJson)
                ActionType.LOG_EVENT ->
                    json.decodeFromString<ActionConfig.LogEvent>(configJson)
                ActionType.EXPORT_DATA ->
                    json.decodeFromString<ActionConfig.ExportData>(configJson)
                ActionType.RUN_WORKFLOW ->
                    json.decodeFromString<ActionConfig.RunWorkflow>(configJson)
                ActionType.WEBHOOK ->
                    json.decodeFromString<ActionConfig.Webhook>(configJson)
                else -> ActionConfig.Empty
            }
        } catch (e: Exception) {
            ActionConfig.Empty
        }
    }
}
