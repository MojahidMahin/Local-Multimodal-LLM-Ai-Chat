package com.localllm.localaichatapp.domain.repository

import com.localllm.localaichatapp.domain.model.Benchmark
import com.localllm.localaichatapp.domain.model.BenchmarkSummary
import com.localllm.localaichatapp.domain.model.TaskType
import kotlinx.coroutines.flow.Flow

interface BenchmarkRepository {
    fun getAllBenchmarks(): Flow<List<Benchmark>>
    fun getBenchmarksByModel(modelId: String): Flow<List<Benchmark>>
    fun getBenchmarksBySession(sessionId: String): Flow<List<Benchmark>>
    fun getBenchmarksByTaskType(taskType: TaskType): Flow<List<Benchmark>>
    fun getRecentBenchmarks(modelId: String, taskType: TaskType, limit: Int = 10): Flow<List<Benchmark>>
    suspend fun insertBenchmark(benchmark: Benchmark)
    suspend fun getAverageTTFT(modelId: String, taskType: TaskType): Float?
    suspend fun getAverageDecodeSpeed(modelId: String, taskType: TaskType): Float?
    suspend fun getAverageLatency(modelId: String, taskType: TaskType): Float?
    suspend fun getBenchmarkSummary(modelId: String, taskType: TaskType): BenchmarkSummary?
    suspend fun deleteOldBenchmarks(cutoffTime: Long)
    suspend fun deleteBenchmarksByModel(modelId: String)
    suspend fun deleteAllBenchmarks()
}