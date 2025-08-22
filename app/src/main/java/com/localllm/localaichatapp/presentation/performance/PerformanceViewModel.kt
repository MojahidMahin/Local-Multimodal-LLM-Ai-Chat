package com.localllm.localaichatapp.presentation.performance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localllm.localaichatapp.domain.model.Benchmark
import com.localllm.localaichatapp.domain.model.Model
import com.localllm.localaichatapp.domain.model.TaskType
import com.localllm.localaichatapp.domain.repository.BenchmarkRepository
import com.localllm.localaichatapp.domain.repository.ChatRepository
import com.localllm.localaichatapp.domain.repository.ModelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class PerformanceMetrics(
    val avgTimeToFirstToken: Float = 0f,
    val avgTokensPerSecond: Float = 0f,
    val totalSessions: Int = 0,
    val avgLatency: Float = 0f,
    val successRate: Float = 0f
)

data class TaskTypePerformance(
    val taskType: TaskType,
    val metrics: PerformanceMetrics,
    val sessionsCount: Int,
    val avgResponseTime: Float
)

data class PerformanceTrend(
    val timestamp: Long,
    val value: Float,
    val label: String
)

data class SystemResources(
    val memoryUsage: Float = 45.2f, // Mock values - would come from Android system
    val cpuUsage: Float = 23.8f,
    val batteryLevel: Float = 78.5f,
    val diskUsage: Float = 62.1f
)

data class PerformanceUiState(
    val isLoading: Boolean = false,
    val currentModel: Model? = null,
    val overallMetrics: PerformanceMetrics = PerformanceMetrics(),
    val taskTypePerformances: List<TaskTypePerformance> = emptyList(),
    val performanceTrends: List<PerformanceTrend> = emptyList(),
    val systemResources: SystemResources = SystemResources(),
    val recentBenchmarks: List<Benchmark> = emptyList(),
    val selectedTimeRange: TimeRange = TimeRange.LAST_7_DAYS,
    val selectedMetric: MetricType = MetricType.TOKENS_PER_SECOND,
    val error: String? = null
)

enum class TimeRange(val displayName: String, val days: Long) {
    LAST_24_HOURS("Last 24 Hours", 1),
    LAST_7_DAYS("Last 7 Days", 7),
    LAST_30_DAYS("Last 30 Days", 30),
    LAST_90_DAYS("Last 90 Days", 90)
}

enum class MetricType(val displayName: String) {
    TIME_TO_FIRST_TOKEN("Time to First Token"),
    TOKENS_PER_SECOND("Tokens per Second"),
    LATENCY("Response Latency"),
    SUCCESS_RATE("Success Rate")
}

@HiltViewModel
class PerformanceViewModel @Inject constructor(
    private val benchmarkRepository: BenchmarkRepository,
    private val modelRepository: ModelRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerformanceUiState())
    val uiState: StateFlow<PerformanceUiState> = _uiState.asStateFlow()

    fun loadPerformanceData(modelId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // Load model information
                val model = modelRepository.getModelById(modelId).first()
                if (model == null) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Model not found"
                        )
                    }
                    return@launch
                }

                // Collect benchmark data and update UI state
                combine(
                    benchmarkRepository.getBenchmarksByModel(modelId),
                    chatRepository.getSessionsByModel(modelId)
                ) { benchmarks, sessions ->
                    val timeRange = _uiState.value.selectedTimeRange
                    val cutoffTime = System.currentTimeMillis() - (timeRange.days * 24 * 60 * 60 * 1000)
                    
                    val filteredBenchmarks = benchmarks.filter { it.timestamp >= cutoffTime }
                    val filteredSessions = sessions.filter { it.createdAt >= cutoffTime }

                    // Calculate overall metrics
                    val overallMetrics = calculateOverallMetrics(filteredBenchmarks)
                    
                    // Calculate task type performance
                    val taskTypePerformances = calculateTaskTypePerformances(filteredBenchmarks, filteredSessions)
                    
                    // Generate performance trends
                    val performanceTrends = generatePerformanceTrends(filteredBenchmarks, _uiState.value.selectedMetric)
                    
                    // Get recent benchmarks
                    val recentBenchmarks = benchmarks.sortedByDescending { it.timestamp }.take(10)
                    
                    // Update system resources (mock data)
                    val systemResources = SystemResources()

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            currentModel = model,
                            overallMetrics = overallMetrics,
                            taskTypePerformances = taskTypePerformances,
                            performanceTrends = performanceTrends,
                            systemResources = systemResources,
                            recentBenchmarks = recentBenchmarks,
                            error = null
                        )
                    }
                }.collect()

            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to load performance data: ${e.message}"
                    )
                }
            }
        }
    }

    fun changeTimeRange(timeRange: TimeRange) {
        _uiState.update { it.copy(selectedTimeRange = timeRange) }
        _uiState.value.currentModel?.let { model ->
            loadPerformanceData(model.id)
        }
    }

    fun changeMetricType(metricType: MetricType) {
        _uiState.update { it.copy(selectedMetric = metricType) }
        _uiState.value.currentModel?.let { model ->
            loadPerformanceData(model.id)
        }
    }

    fun runBenchmark(modelId: String) {
        viewModelScope.launch {
            try {
                // Create a new benchmark
                val benchmark = Benchmark(
                    id = "benchmark_${System.currentTimeMillis()}",
                    modelId = modelId,
                    sessionId = "session_${System.currentTimeMillis()}",
                    taskType = TaskType.CHAT,
                    ttftMs = (100..300).random().toLong(),
                    decodeSpeedTokensPerSecond = (15..45).random().toFloat(),
                    totalLatencyMs = (2000..5000).random().toLong(),
                    inputTokenCount = 50,
                    outputTokenCount = 150,
                    memoryUsageMb = (200..800).random().toFloat(),
                    cpuUsagePercent = (10..80).random().toFloat(),
                    batteryLevel = (60..100).random().toFloat(),
                    timestamp = System.currentTimeMillis()
                )

                benchmarkRepository.insertBenchmark(benchmark)
                
                // Reload data to reflect new benchmark
                loadPerformanceData(modelId)

            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = "Failed to run benchmark: ${e.message}")
                }
            }
        }
    }

    fun clearBenchmarks(modelId: String) {
        viewModelScope.launch {
            try {
                benchmarkRepository.deleteBenchmarksByModel(modelId)
                loadPerformanceData(modelId)
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = "Failed to clear benchmarks: ${e.message}")
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun calculateOverallMetrics(benchmarks: List<Benchmark>): PerformanceMetrics {
        if (benchmarks.isEmpty()) return PerformanceMetrics()

        val avgTTFT = benchmarks.map { it.ttftMs.toFloat() }.average().toFloat()
        val avgTokensPerSec = benchmarks.map { it.decodeSpeedTokensPerSecond }.average().toFloat()
        val totalSessions = benchmarks.distinctBy { it.sessionId }.size
        val avgLatency = benchmarks.map { it.totalLatencyMs.toFloat() }.average().toFloat()
        val successRate = 100f // Mock - would calculate based on failed vs successful generations

        return PerformanceMetrics(
            avgTimeToFirstToken = avgTTFT,
            avgTokensPerSecond = avgTokensPerSec,
            totalSessions = totalSessions,
            avgLatency = avgLatency,
            successRate = successRate
        )
    }

    private fun calculateTaskTypePerformances(
        benchmarks: List<Benchmark>,
        sessions: List<com.localllm.localaichatapp.domain.model.ChatSession>
    ): List<TaskTypePerformance> {
        return TaskType.values().map { taskType ->
            val taskBenchmarks = benchmarks.filter { it.taskType == taskType }
            val taskSessions = sessions.filter { it.taskType == taskType }
            
            val metrics = if (taskBenchmarks.isNotEmpty()) {
                PerformanceMetrics(
                    avgTimeToFirstToken = taskBenchmarks.map { it.ttftMs.toFloat() }.average().toFloat(),
                    avgTokensPerSecond = taskBenchmarks.map { it.decodeSpeedTokensPerSecond }.average().toFloat(),
                    totalSessions = taskSessions.size,
                    avgLatency = taskBenchmarks.map { it.totalLatencyMs.toFloat() }.average().toFloat(),
                    successRate = 98f // Mock success rate
                )
            } else {
                PerformanceMetrics()
            }

            val avgResponseTime = if (taskBenchmarks.isNotEmpty()) {
                taskBenchmarks.map { it.totalLatencyMs.toFloat() }.average().toFloat()
            } else 0f

            TaskTypePerformance(
                taskType = taskType,
                metrics = metrics,
                sessionsCount = taskSessions.size,
                avgResponseTime = avgResponseTime
            )
        }
    }

    private fun generatePerformanceTrends(
        benchmarks: List<Benchmark>,
        metricType: MetricType
    ): List<PerformanceTrend> {
        if (benchmarks.isEmpty()) return emptyList()

        // Group benchmarks by day and calculate averages
        val groupedByDay = benchmarks
            .sortedBy { it.timestamp }
            .groupBy { 
                // Use Calendar for API 24+ compatibility
                val calendar = java.util.Calendar.getInstance()
                calendar.timeInMillis = it.timestamp
                "${calendar.get(java.util.Calendar.YEAR)}-${calendar.get(java.util.Calendar.MONTH) + 1}-${calendar.get(java.util.Calendar.DAY_OF_MONTH)}"
            }

        return groupedByDay.map { (dateStr, dayBenchmarks) ->
            val value = when (metricType) {
                MetricType.TIME_TO_FIRST_TOKEN -> dayBenchmarks.map { it.ttftMs.toFloat() }.average().toFloat()
                MetricType.TOKENS_PER_SECOND -> dayBenchmarks.map { it.decodeSpeedTokensPerSecond }.average().toFloat()
                MetricType.LATENCY -> dayBenchmarks.map { it.totalLatencyMs.toFloat() }.average().toFloat()
                MetricType.SUCCESS_RATE -> 98f // Mock success rate
            }

            val timestamp = dayBenchmarks.first().timestamp
            val formatter = DateTimeFormatter.ofPattern("MMM dd")
            val label = java.time.Instant.ofEpochMilli(timestamp)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate()
                .format(formatter)

            PerformanceTrend(
                timestamp = timestamp,
                value = value,
                label = label
            )
        }.takeLast(30) // Show last 30 data points
    }

    fun exportPerformanceData(modelId: String) {
        viewModelScope.launch {
            try {
                val benchmarks = benchmarkRepository.getBenchmarksByModel(modelId).first()
                val currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))
                
                // Mock export - in real implementation would create CSV/JSON file
                val exportData = """
                    Performance Export - ${_uiState.value.currentModel?.name}
                    Generated: $currentTime
                    
                    Overall Metrics:
                    - Avg TTFT: ${_uiState.value.overallMetrics.avgTimeToFirstToken}ms
                    - Avg Tokens/sec: ${_uiState.value.overallMetrics.avgTokensPerSecond}
                    - Total Sessions: ${_uiState.value.overallMetrics.totalSessions}
                    - Avg Latency: ${_uiState.value.overallMetrics.avgLatency}ms
                    
                    Detailed Benchmarks: ${benchmarks.size} entries
                """.trimIndent()

                // In real implementation, would save to file and show share intent
                _uiState.update { 
                    it.copy(error = "Export functionality would save data to Downloads folder")
                }

            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = "Failed to export data: ${e.message}")
                }
            }
        }
    }
}