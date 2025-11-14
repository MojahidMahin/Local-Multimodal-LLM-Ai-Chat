package com.localllm.localaichatapp.domain.model.automation

import kotlinx.serialization.Serializable

/**
 * Represents a condition that controls workflow execution.
 */
@Serializable
data class Condition(
    val id: String,
    val type: ConditionType,
    val config: ConditionConfig,
    val operator: LogicalOperator = LogicalOperator.AND
)

/**
 * Logical operators for combining conditions.
 */
enum class LogicalOperator {
    AND,
    OR,
    NOT
}

/**
 * Base interface for condition configurations.
 */
@Serializable
sealed class ConditionConfig {
    /**
     * Compare values condition.
     */
    @Serializable
    data class Compare(
        val leftValue: String,
        val rightValue: String,
        val comparisonType: String = "EQUALS"
    ) : ConditionConfig()

    /**
     * Contains text condition.
     */
    @Serializable
    data class Contains(
        val text: String,
        val keywords: List<String>,
        val caseSensitive: Boolean = false
    ) : ConditionConfig()

    /**
     * Time range condition.
     */
    @Serializable
    data class TimeRange(
        val startTime: String, // HH:mm
        val endTime: String // HH:mm
    ) : ConditionConfig()

    /**
     * Day of week condition.
     */
    @Serializable
    data class DayOfWeek(
        val days: List<Int> // 1-7 (Mon-Sun)
    ) : ConditionConfig()

    /**
     * Battery level condition.
     */
    @Serializable
    data class BatteryLevel(
        val minLevel: Int? = null,
        val maxLevel: Int? = null,
        val isCharging: Boolean? = null
    ) : ConditionConfig()

    /**
     * Network availability condition.
     */
    @Serializable
    data class NetworkAvailable(
        val requireWifi: Boolean = false
    ) : ConditionConfig()

    /**
     * Storage available condition.
     */
    @Serializable
    data class StorageAvailable(
        val minMb: Long
    ) : ConditionConfig()

    /**
     * Count threshold condition.
     */
    @Serializable
    data class CountThreshold(
        val countType: String, // SESSIONS, MESSAGES, MODELS
        val threshold: Int,
        val operator: String = "GREATER_THAN"
    ) : ConditionConfig()

    /**
     * Regex match condition.
     */
    @Serializable
    data class RegexMatch(
        val text: String,
        val pattern: String
    ) : ConditionConfig()

    /**
     * Model active condition.
     */
    @Serializable
    data class ModelActive(
        val modelId: String
    ) : ConditionConfig()

    /**
     * Group of conditions (for AND/OR logic).
     */
    @Serializable
    data class Group(
        val conditions: List<Condition>,
        val operator: LogicalOperator = LogicalOperator.AND
    ) : ConditionConfig()

    /**
     * No configuration needed.
     */
    @Serializable
    object Empty : ConditionConfig()
}
