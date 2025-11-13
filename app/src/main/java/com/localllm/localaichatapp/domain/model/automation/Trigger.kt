package com.localllm.localaichatapp.domain.model.automation

import kotlinx.serialization.Serializable

/**
 * Represents a trigger that initiates a workflow.
 */
@Serializable
data class Trigger(
    val type: TriggerType,
    val config: TriggerConfig
)

/**
 * Base interface for trigger configurations.
 */
@Serializable
sealed class TriggerConfig {
    /**
     * Scheduled trigger configuration.
     */
    @Serializable
    data class Scheduled(
        val time: String, // HH:mm format
        val daysOfWeek: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7), // 1-7 (Mon-Sun)
        val enabled: Boolean = true
    ) : TriggerConfig()

    /**
     * Message count threshold trigger.
     */
    @Serializable
    data class MessageThreshold(
        val count: Int,
        val sessionId: String? = null // null for any session
    ) : TriggerConfig()

    /**
     * Storage threshold trigger.
     */
    @Serializable
    data class StorageThreshold(
        val thresholdMb: Long,
        val checkIntervalMinutes: Int = 60
    ) : TriggerConfig()

    /**
     * Battery level trigger.
     */
    @Serializable
    data class BatteryLevel(
        val threshold: Int, // 0-100
        val operator: String = "LESS_THAN" // LESS_THAN, GREATER_THAN
    ) : TriggerConfig()

    /**
     * Performance threshold trigger.
     */
    @Serializable
    data class PerformanceThreshold(
        val metricType: String, // TTFT, MEMORY, CPU
        val threshold: Double,
        val operator: String = "GREATER_THAN"
    ) : TriggerConfig()

    /**
     * Event-based trigger (no additional config needed).
     */
    @Serializable
    object Event : TriggerConfig()
}
