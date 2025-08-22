package com.localllm.localaichatapp.domain.model

data class ResponseMetadata(
    val tokenCount: Int = 0,
    val responseTimeMs: Float = 0f,
    val ttftMs: Float = 0f,
    val tokensPerSecond: Float = 0f,
    val modelUsed: String = "",
    val memoryUsageMb: Float = 0f,
    val cpuUsagePercent: Float = 0f,
    val batteryLevel: Float? = null,
    val temperature: Float = 0.7f,
    val maxTokens: Int = 1000,
    val finishReason: String = "stop",
    val isComplete: Boolean = true
) {
    val formattedTtft: String
        get() = if (ttftMs > 0) "${ttftMs.toInt()}ms" else "N/A"
    
    val formattedDecodeSpeed: String
        get() = if (tokensPerSecond > 0) "${tokensPerSecond.toInt()} tok/s" else "N/A"
    
    val tokensGenerated: String
        get() = if (tokenCount > 0) "$tokenCount tokens" else "N/A"
}