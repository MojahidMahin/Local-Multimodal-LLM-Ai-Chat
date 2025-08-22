package com.localllm.localaichatapp.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.localllm.localaichatapp.data.local.database.entity.BenchmarkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BenchmarkDao {
    
    @Query("SELECT * FROM benchmarks ORDER BY timestamp DESC")
    fun getAllBenchmarks(): Flow<List<BenchmarkEntity>>
    
    @Query("SELECT * FROM benchmarks WHERE modelId = :modelId ORDER BY timestamp DESC")
    fun getBenchmarksByModel(modelId: String): Flow<List<BenchmarkEntity>>
    
    @Query("SELECT * FROM benchmarks WHERE sessionId = :sessionId ORDER BY timestamp DESC")
    fun getBenchmarksBySession(sessionId: String): Flow<List<BenchmarkEntity>>
    
    @Query("SELECT * FROM benchmarks WHERE taskType = :taskType ORDER BY timestamp DESC")
    fun getBenchmarksByTaskType(taskType: String): Flow<List<BenchmarkEntity>>
    
    @Query("SELECT AVG(ttftMs) FROM benchmarks WHERE modelId = :modelId AND taskType = :taskType")
    suspend fun getAverageTTFT(modelId: String, taskType: String): Float?
    
    @Query("SELECT AVG(decodeSpeedTokensPerSecond) FROM benchmarks WHERE modelId = :modelId AND taskType = :taskType")
    suspend fun getAverageDecodeSpeed(modelId: String, taskType: String): Float?
    
    @Query("SELECT AVG(totalLatencyMs) FROM benchmarks WHERE modelId = :modelId AND taskType = :taskType")
    suspend fun getAverageLatency(modelId: String, taskType: String): Float?
    
    @Query("SELECT * FROM benchmarks WHERE modelId = :modelId AND taskType = :taskType ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentBenchmarks(modelId: String, taskType: String, limit: Int = 10): Flow<List<BenchmarkEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBenchmark(benchmark: BenchmarkEntity)
    
    @Query("DELETE FROM benchmarks WHERE timestamp < :cutoffTime")
    suspend fun deleteOldBenchmarks(cutoffTime: Long)
    
    @Query("DELETE FROM benchmarks")
    suspend fun deleteAllBenchmarks()
}