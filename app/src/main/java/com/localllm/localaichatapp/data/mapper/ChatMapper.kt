package com.localllm.localaichatapp.data.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.localllm.localaichatapp.data.local.database.entity.ChatMessageEntity
import com.localllm.localaichatapp.data.local.database.entity.ChatSessionEntity
import com.localllm.localaichatapp.domain.model.*

object ChatMapper {
    private val gson = Gson()
    
    fun ChatSessionEntity.toDomain(messages: List<ChatMessage> = emptyList()): ChatSession {
        val taskType = try {
            TaskType.valueOf(taskType)
        } catch (e: Exception) {
            TaskType.CHAT
        }
        
        return ChatSession(
            id = id,
            title = title,
            modelId = modelId,
            taskType = taskType,
            messages = messages,
            createdAt = createdAt,
            updatedAt = updatedAt,
            messageCount = messageCount,
            isBookmarked = isBookmarked,
            tags = tags
        )
    }
    
    fun ChatSession.toEntity(): ChatSessionEntity {
        return ChatSessionEntity(
            id = id,
            title = title,
            modelId = modelId,
            taskType = taskType.name,
            createdAt = createdAt,
            updatedAt = updatedAt,
            messageCount = messageCount,
            isBookmarked = isBookmarked,
            tags = tags
        )
    }
    
    fun ChatMessageEntity.toDomain(): ChatMessage {
        val metadataMap = try {
            metadata?.let { 
                gson.fromJson<Map<String, Any>>(it, object : TypeToken<Map<String, Any>>() {}.type)
            }
        } catch (e: Exception) {
            null
        }
        
        return when (messageType) {
            "TEXT" -> TextMessage(
                id = id,
                timestamp = timestamp,
                sender = sender,
                content = content,
                isStreaming = isStreaming,
                tokenCount = tokenCount,
                responseTimeMs = responseTimeMs,
                metadata = metadataMap
            )
            "LOADING" -> LoadingMessage(
                id = id,
                timestamp = timestamp,
                sender = sender,
                tokenCount = tokenCount,
                responseTimeMs = responseTimeMs
            )
            "ERROR" -> ErrorMessage(
                id = id,
                timestamp = timestamp,
                sender = sender,
                error = content,
                tokenCount = tokenCount,
                responseTimeMs = responseTimeMs
            )
            "IMAGE" -> ImageMessage(
                id = id,
                timestamp = timestamp,
                sender = sender,
                imageUri = imageUri ?: "",
                caption = content.takeIf { it.isNotBlank() },
                analysisResult = metadataMap?.get("analysisResult") as? String,
                tokenCount = tokenCount,
                responseTimeMs = responseTimeMs
            )
            "AUDIO" -> AudioMessage(
                id = id,
                timestamp = timestamp,
                sender = sender,
                audioUri = audioUri ?: "",
                duration = metadataMap?.get("duration") as? Long,
                transcription = metadataMap?.get("transcription") as? String,
                analysisResult = metadataMap?.get("analysisResult") as? String,
                tokenCount = tokenCount,
                responseTimeMs = responseTimeMs
            )
            "BENCHMARK" -> {
                val benchmarkData = metadataMap?.get("benchmark")
                if (benchmarkData != null) {
                    try {
                        val benchmark = gson.fromJson(gson.toJson(benchmarkData), Benchmark::class.java)
                        BenchmarkMessage(
                            id = id,
                            timestamp = timestamp,
                            sender = sender,
                            benchmark = benchmark,
                            tokenCount = tokenCount,
                            responseTimeMs = responseTimeMs
                        )
                    } catch (e: Exception) {
                        ErrorMessage(id = id, timestamp = timestamp, error = "Invalid benchmark data")
                    }
                } else {
                    ErrorMessage(id = id, timestamp = timestamp, error = "Missing benchmark data")
                }
            }
            else -> TextMessage(
                id = id,
                timestamp = timestamp,
                sender = sender,
                content = content,
                isStreaming = isStreaming,
                tokenCount = tokenCount,
                responseTimeMs = responseTimeMs,
                metadata = metadataMap
            )
        }
    }
    
    fun ChatMessage.toEntity(sessionId: String): ChatMessageEntity {
        return when (this) {
            is TextMessage -> ChatMessageEntity(
                id = id,
                sessionId = sessionId,
                content = content,
                sender = sender,
                timestamp = timestamp,
                messageType = "TEXT",
                isStreaming = isStreaming,
                metadata = metadata?.let { gson.toJson(it) },
                tokenCount = tokenCount,
                responseTimeMs = responseTimeMs
            )
            is LoadingMessage -> ChatMessageEntity(
                id = id,
                sessionId = sessionId,
                content = "",
                sender = sender,
                timestamp = timestamp,
                messageType = "LOADING",
                tokenCount = tokenCount,
                responseTimeMs = responseTimeMs
            )
            is ErrorMessage -> ChatMessageEntity(
                id = id,
                sessionId = sessionId,
                content = error,
                sender = sender,
                timestamp = timestamp,
                messageType = "ERROR",
                tokenCount = tokenCount,
                responseTimeMs = responseTimeMs
            )
            is ImageMessage -> ChatMessageEntity(
                id = id,
                sessionId = sessionId,
                content = caption ?: "",
                sender = sender,
                timestamp = timestamp,
                messageType = "IMAGE",
                imageUri = imageUri,
                metadata = gson.toJson(mapOf(
                    "analysisResult" to analysisResult
                ).filterValues { it != null }),
                tokenCount = tokenCount,
                responseTimeMs = responseTimeMs
            )
            is AudioMessage -> ChatMessageEntity(
                id = id,
                sessionId = sessionId,
                content = transcription ?: "",
                sender = sender,
                timestamp = timestamp,
                messageType = "AUDIO",
                audioUri = audioUri,
                metadata = gson.toJson(mapOf(
                    "duration" to duration,
                    "transcription" to transcription,
                    "analysisResult" to analysisResult
                ).filterValues { it != null }),
                tokenCount = tokenCount,
                responseTimeMs = responseTimeMs
            )
            is BenchmarkMessage -> ChatMessageEntity(
                id = id,
                sessionId = sessionId,
                content = "Benchmark Results",
                sender = sender,
                timestamp = timestamp,
                messageType = "BENCHMARK",
                metadata = gson.toJson(mapOf("benchmark" to benchmark)),
                tokenCount = tokenCount,
                responseTimeMs = responseTimeMs
            )
        }
    }
}
}