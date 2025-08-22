package com.localllm.localaichatapp.domain.model

data class Benchmark(
    val id: String,
    val modelId: String,
    val sessionId: String,
    val taskType: TaskType,
    val ttftMs: Long, // Time to First Token
    val decodeSpeedTokensPerSecond: Float,
    val totalLatencyMs: Long,
    val inputTokenCount: Int,
    val outputTokenCount: Int,
    val memoryUsageMb: Float,
    val cpuUsagePercent: Float,
    val batteryLevel: Float? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    val formattedTTFT: String
        get() = "${ttftMs}ms"
    
    val formattedDecodeSpeed: String
        get() = String.format("%.2f tokens/s", decodeSpeedTokensPerSecond)
    
    val formattedLatency: String
        get() = "${totalLatencyMs}ms"
    
    val formattedMemory: String
        get() = String.format("%.1f MB", memoryUsageMb)
    
    val formattedCPU: String
        get() = String.format("%.1f%%", cpuUsagePercent)
    
    val tokensPerSecond: Float
        get() = if (totalLatencyMs > 0) {
            (outputTokenCount * 1000f) / totalLatencyMs
        } else 0f
}

data class BenchmarkSummary(
    val modelId: String,
    val taskType: TaskType,
    val averageTTFT: Float,
    val averageDecodeSpeed: Float,
    val averageLatency: Float,
    val totalSessions: Int,
    val totalTokensGenerated: Int,
    val lastBenchmarkTime: Long
)