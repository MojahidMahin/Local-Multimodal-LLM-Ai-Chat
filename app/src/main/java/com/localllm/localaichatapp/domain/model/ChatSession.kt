package com.localllm.localaichatapp.domain.model

import kotlinx.coroutines.flow.Flow

data class ChatSession(
    val id: String,
    val title: String,
    val modelId: String,
    val taskType: TaskType,
    val messages: List<ChatMessage> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val messageCount: Int = 0,
    val isBookmarked: Boolean = false,
    val tags: List<String> = emptyList()
) {
    val isEmpty: Boolean
        get() = messages.isEmpty()
    
    val lastMessage: ChatMessage?
        get() = messages.lastOrNull()
    
    val preview: String
        get() = when (val last = lastMessage) {
            is TextMessage -> last.content.take(100)
            is ImageMessage -> "📸 Image: ${last.caption ?: "No caption"}"
            is AudioMessage -> "🎵 Audio: ${last.transcription ?: "No transcription"}"
            is ErrorMessage -> "❌ Error: ${last.error}"
            is BenchmarkMessage -> "📊 Benchmark results"
            else -> "Empty conversation"
        }
}

data class StreamingResponse(
    val content: String,
    val isComplete: Boolean,
    val metadata: ResponseMetadata? = null
)

data class ResponseMetadata(
    val timeToFirstToken: Long? = null,
    val tokensPerSecond: Float? = null,
    val totalInputTokens: Int? = null,
    val totalOutputTokens: Int? = null,
    val latencyMs: Long? = null,
    val memoryUsageMb: Float? = null,
    val cpuUsagePercent: Float? = null,
    val batteryLevel: Float? = null
) {
    fun toBenchmark(
        modelId: String,
        sessionId: String,
        taskType: TaskType
    ): Benchmark? {
        return if (timeToFirstToken != null && tokensPerSecond != null && latencyMs != null) {
            Benchmark(
                id = java.util.UUID.randomUUID().toString(),
                modelId = modelId,
                sessionId = sessionId,
                taskType = taskType,
                ttftMs = timeToFirstToken,
                decodeSpeedTokensPerSecond = tokensPerSecond,
                totalLatencyMs = latencyMs,
                inputTokenCount = totalInputTokens ?: 0,
                outputTokenCount = totalOutputTokens ?: 0,
                memoryUsageMb = memoryUsageMb ?: 0f,
                cpuUsagePercent = cpuUsagePercent ?: 0f,
                batteryLevel = batteryLevel
            )
        } else null
    }
}