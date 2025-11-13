package com.localllm.localaichatapp.domain.service

import android.content.Context
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Environment
import android.os.StatFs
import com.localllm.localaichatapp.domain.model.automation.Condition
import com.localllm.localaichatapp.domain.model.automation.ConditionConfig
import com.localllm.localaichatapp.domain.model.automation.ConditionType
import com.localllm.localaichatapp.domain.model.automation.WorkflowContext
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Evaluates conditions for workflows.
 */
@Singleton
class ConditionEvaluator @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Evaluate a single condition.
     */
    suspend fun evaluate(condition: Condition, workflowContext: WorkflowContext): Boolean {
        return when (condition.type) {
            ConditionType.EQUALS -> evaluateEquals(condition.config, workflowContext)
            ConditionType.NOT_EQUALS -> !evaluateEquals(condition.config, workflowContext)
            ConditionType.CONTAINS -> evaluateContains(condition.config, workflowContext)
            ConditionType.NOT_CONTAINS -> !evaluateContains(condition.config, workflowContext)
            ConditionType.GREATER_THAN -> evaluateGreaterThan(condition.config, workflowContext)
            ConditionType.LESS_THAN -> evaluateLessThan(condition.config, workflowContext)
            ConditionType.TIME_OF_DAY -> evaluateTimeOfDay(condition.config)
            ConditionType.DAY_OF_WEEK -> evaluateDayOfWeek(condition.config)
            ConditionType.BATTERY_LEVEL -> evaluateBatteryLevel(condition.config)
            ConditionType.STORAGE_AVAILABLE -> evaluateStorageAvailable(condition.config)
            ConditionType.REGEX_MATCH -> evaluateRegexMatch(condition.config, workflowContext)
            else -> true // Default to true for unimplemented conditions
        }
    }

    private fun evaluateEquals(config: ConditionConfig, context: WorkflowContext): Boolean {
        return when (config) {
            is ConditionConfig.Compare -> {
                val left = resolveValue(config.leftValue, context)
                val right = resolveValue(config.rightValue, context)
                left == right
            }
            else -> false
        }
    }

    private fun evaluateContains(config: ConditionConfig, context: WorkflowContext): Boolean {
        return when (config) {
            is ConditionConfig.Contains -> {
                val text = resolveValue(config.text, context)
                config.keywords.any { keyword ->
                    if (config.caseSensitive) {
                        text.contains(keyword)
                    } else {
                        text.contains(keyword, ignoreCase = true)
                    }
                }
            }
            else -> false
        }
    }

    private fun evaluateGreaterThan(config: ConditionConfig, context: WorkflowContext): Boolean {
        return when (config) {
            is ConditionConfig.Compare -> {
                val left = resolveValue(config.leftValue, context).toDoubleOrNull() ?: 0.0
                val right = resolveValue(config.rightValue, context).toDoubleOrNull() ?: 0.0
                left > right
            }
            else -> false
        }
    }

    private fun evaluateLessThan(config: ConditionConfig, context: WorkflowContext): Boolean {
        return when (config) {
            is ConditionConfig.Compare -> {
                val left = resolveValue(config.leftValue, context).toDoubleOrNull() ?: 0.0
                val right = resolveValue(config.rightValue, context).toDoubleOrNull() ?: 0.0
                left < right
            }
            else -> false
        }
    }

    private fun evaluateTimeOfDay(config: ConditionConfig): Boolean {
        return when (config) {
            is ConditionConfig.TimeRange -> {
                val now = Calendar.getInstance()
                val currentHour = now.get(Calendar.HOUR_OF_DAY)
                val currentMinute = now.get(Calendar.MINUTE)
                val currentTime = currentHour * 60 + currentMinute

                val (startHour, startMinute) = config.startTime.split(":").map { it.toInt() }
                val (endHour, endMinute) = config.endTime.split(":").map { it.toInt() }

                val startTime = startHour * 60 + startMinute
                val endTime = endHour * 60 + endMinute

                currentTime in startTime..endTime
            }
            else -> false
        }
    }

    private fun evaluateDayOfWeek(config: ConditionConfig): Boolean {
        return when (config) {
            is ConditionConfig.DayOfWeek -> {
                val now = Calendar.getInstance()
                val dayOfWeek = now.get(Calendar.DAY_OF_WEEK)
                // Calendar.DAY_OF_WEEK: 1 (Sunday) to 7 (Saturday)
                // Convert to: 1 (Monday) to 7 (Sunday)
                val adjustedDay = if (dayOfWeek == 1) 7 else dayOfWeek - 1
                adjustedDay in config.days
            }
            else -> false
        }
    }

    private fun evaluateBatteryLevel(config: ConditionConfig): Boolean {
        return when (config) {
            is ConditionConfig.BatteryLevel -> {
                val batteryStatus = context.registerReceiver(
                    null,
                    IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED)
                )

                val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
                val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
                val batteryPct = if (level >= 0 && scale > 0) {
                    (level * 100 / scale.toFloat()).toInt()
                } else {
                    -1
                }

                val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL

                var result = true

                if (config.minLevel != null && batteryPct < config.minLevel) {
                    result = false
                }

                if (config.maxLevel != null && batteryPct > config.maxLevel) {
                    result = false
                }

                if (config.isCharging != null && isCharging != config.isCharging) {
                    result = false
                }

                result
            }
            else -> false
        }
    }

    private fun evaluateStorageAvailable(config: ConditionConfig): Boolean {
        return when (config) {
            is ConditionConfig.StorageAvailable -> {
                val stat = StatFs(Environment.getDataDirectory().path)
                val availableBytes = stat.availableBlocksLong * stat.blockSizeLong
                val availableMb = availableBytes / (1024 * 1024)
                availableMb >= config.minMb
            }
            else -> false
        }
    }

    private fun evaluateRegexMatch(config: ConditionConfig, context: WorkflowContext): Boolean {
        return when (config) {
            is ConditionConfig.RegexMatch -> {
                val text = resolveValue(config.text, context)
                val regex = Regex(config.pattern)
                regex.matches(text)
            }
            else -> false
        }
    }

    /**
     * Resolve a value from context or return literal.
     */
    private fun resolveValue(value: String, context: WorkflowContext): String {
        // If value starts with "$", treat as context variable
        return if (value.startsWith("$")) {
            val key = value.substring(1)
            context.get<Any>(key)?.toString() ?: value
        } else {
            value
        }
    }
}
