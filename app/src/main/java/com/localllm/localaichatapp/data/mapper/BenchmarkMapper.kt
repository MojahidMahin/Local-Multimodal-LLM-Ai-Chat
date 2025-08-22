package com.localllm.localaichatapp.data.mapper

import com.localllm.localaichatapp.data.local.database.entity.BenchmarkEntity
import com.localllm.localaichatapp.domain.model.Benchmark
import com.localllm.localaichatapp.domain.model.TaskType

class BenchmarkMapper {
    
    fun toEntity(domain: Benchmark): BenchmarkEntity {
        return BenchmarkEntity(
            id = domain.id,
            modelId = domain.modelId,
            sessionId = domain.sessionId,
            taskType = domain.taskType.name,
            ttftMs = domain.ttftMs,
            decodeSpeedTokensPerSecond = domain.decodeSpeedTokensPerSecond,
            totalLatencyMs = domain.totalLatencyMs,
            inputTokenCount = domain.inputTokenCount,
            outputTokenCount = domain.outputTokenCount,
            memoryUsageMb = domain.memoryUsageMb,
            cpuUsagePercent = domain.cpuUsagePercent,
            batteryLevel = domain.batteryLevel,
            timestamp = domain.timestamp
        )
    }
    
    fun toDomain(entity: BenchmarkEntity): Benchmark {
        val taskType = try {
            TaskType.valueOf(entity.taskType)
        } catch (e: Exception) {
            TaskType.CHAT
        }
        
        return Benchmark(
            id = entity.id,
            modelId = entity.modelId,
            sessionId = entity.sessionId,
            taskType = taskType,
            ttftMs = entity.ttftMs,
            decodeSpeedTokensPerSecond = entity.decodeSpeedTokensPerSecond,
            totalLatencyMs = entity.totalLatencyMs,
            inputTokenCount = entity.inputTokenCount,
            outputTokenCount = entity.outputTokenCount,
            memoryUsageMb = entity.memoryUsageMb,
            cpuUsagePercent = entity.cpuUsagePercent,
            batteryLevel = entity.batteryLevel,
            timestamp = entity.timestamp
        )
    }
    
    fun toDomainList(entities: List<BenchmarkEntity>): List<Benchmark> {
        return entities.map { toDomain(it) }
    }
    
    fun toEntityList(domains: List<Benchmark>): List<BenchmarkEntity> {
        return domains.map { toEntity(it) }
    }
}