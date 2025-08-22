package com.localllm.localaichatapp.data.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "benchmarks",
    foreignKeys = [
        ForeignKey(
            entity = ModelEntity::class,
            parentColumns = ["id"],
            childColumns = ["modelId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ChatSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["modelId"]), Index(value = ["sessionId"])]
)
data class BenchmarkEntity(
    @PrimaryKey
    val id: String,
    val modelId: String,
    val sessionId: String,
    val taskType: String,
    val ttftMs: Long, // Time to First Token
    val decodeSpeedTokensPerSecond: Float,
    val totalLatencyMs: Long,
    val inputTokenCount: Int,
    val outputTokenCount: Int,
    val memoryUsageMb: Float,
    val cpuUsagePercent: Float,
    val batteryLevel: Float?,
    val timestamp: Long = System.currentTimeMillis()
)