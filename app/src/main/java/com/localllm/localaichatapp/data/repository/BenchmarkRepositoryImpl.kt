package com.localllm.localaichatapp.data.repository

import com.localllm.localaichatapp.data.local.database.dao.BenchmarkDao
import com.localllm.localaichatapp.data.mapper.BenchmarkMapper
import com.localllm.localaichatapp.domain.model.Benchmark
import com.localllm.localaichatapp.domain.model.BenchmarkSummary
import com.localllm.localaichatapp.domain.model.TaskType
import com.localllm.localaichatapp.domain.repository.BenchmarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BenchmarkRepositoryImpl @Inject constructor(
    private val benchmarkDao: BenchmarkDao,
    private val benchmarkMapper: BenchmarkMapper
) : BenchmarkRepository {
    
    override fun getAllBenchmarks(): Flow<List<Benchmark>> {
        return benchmarkDao.getAllBenchmarks().map { entities ->
            benchmarkMapper.toDomainList(entities)
        }
    }
    
    override fun getBenchmarksByModel(modelId: String): Flow<List<Benchmark>> {
        return benchmarkDao.getBenchmarksByModel(modelId).map { entities ->
            benchmarkMapper.toDomainList(entities)
        }
    }
    
    override fun getBenchmarksBySession(sessionId: String): Flow<List<Benchmark>> {
        return benchmarkDao.getBenchmarksBySession(sessionId).map { entities ->
            benchmarkMapper.toDomainList(entities)
        }
    }
    
    override fun getBenchmarksByTaskType(taskType: TaskType): Flow<List<Benchmark>> {
        return benchmarkDao.getBenchmarksByTaskType(taskType.name).map { entities ->
            benchmarkMapper.toDomainList(entities)
        }
    }
    
    override fun getRecentBenchmarks(modelId: String, taskType: TaskType, limit: Int): Flow<List<Benchmark>> {
        return benchmarkDao.getRecentBenchmarks(modelId, taskType.name, limit).map { entities ->
            benchmarkMapper.toDomainList(entities)
        }
    }
    
    override suspend fun insertBenchmark(benchmark: Benchmark) {
        benchmarkDao.insertBenchmark(benchmarkMapper.toEntity(benchmark))
    }
    
    override suspend fun getAverageTTFT(modelId: String, taskType: TaskType): Float? {
        return benchmarkDao.getAverageTTFT(modelId, taskType.name)
    }
    
    override suspend fun getAverageDecodeSpeed(modelId: String, taskType: TaskType): Float? {
        return benchmarkDao.getAverageDecodeSpeed(modelId, taskType.name)
    }
    
    override suspend fun getAverageLatency(modelId: String, taskType: TaskType): Float? {
        return benchmarkDao.getAverageLatency(modelId, taskType.name)
    }
    
    override suspend fun getBenchmarkSummary(modelId: String, taskType: TaskType): BenchmarkSummary? {
        val avgTTFT = getAverageTTFT(modelId, taskType)
        val avgDecodeSpeed = getAverageDecodeSpeed(modelId, taskType)
        val avgLatency = getAverageLatency(modelId, taskType)
        
        if (avgTTFT == null && avgDecodeSpeed == null && avgLatency == null) {
            return null
        }
        
        // Get additional stats from recent benchmarks
        val recentBenchmarks = benchmarkDao.getRecentBenchmarks(modelId, taskType.name, 50)
            .map { entities -> benchmarkMapper.toDomainList(entities) }
        
        // This is a simplified implementation - in practice, you'd collect these over time
        return BenchmarkSummary(
            modelId = modelId,
            taskType = taskType,
            averageTTFT = avgTTFT ?: 0f,
            averageDecodeSpeed = avgDecodeSpeed ?: 0f,
            averageLatency = avgLatency ?: 0f,
            totalSessions = 0, // Would need to count unique sessions
            totalTokensGenerated = 0, // Would need to sum from benchmarks
            lastBenchmarkTime = System.currentTimeMillis()
        )
    }
    
    override suspend fun deleteOldBenchmarks(cutoffTime: Long) {
        benchmarkDao.deleteOldBenchmarks(cutoffTime)
    }
    
    override suspend fun deleteBenchmarksByModel(modelId: String) {
        benchmarkDao.deleteBenchmarksByModel(modelId)
    }
    
    override suspend fun deleteAllBenchmarks() {
        benchmarkDao.deleteAllBenchmarks()
    }
}