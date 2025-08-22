package com.localllm.localaichatapp.data.mapper

import com.google.gson.Gson
import com.localllm.localaichatapp.data.local.database.entity.ChatMessageEntity
import com.localllm.localaichatapp.data.local.database.entity.ChatSessionEntity
import com.localllm.localaichatapp.domain.model.ChatMessage
import com.localllm.localaichatapp.domain.model.ChatSender
import com.localllm.localaichatapp.domain.model.ChatSession
import com.localllm.localaichatapp.domain.model.ResponseMetadata
import com.localllm.localaichatapp.domain.model.TaskType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatMapper @Inject constructor(
    private val gson: Gson
) {
    
    fun toDomain(entity: ChatSessionEntity): ChatSession {
        val taskType = try {
            TaskType.valueOf(entity.taskType)
        } catch (e: Exception) {
            TaskType.CHAT
        }
        
        val tags = try {
            gson.fromJson(entity.tags, Array<String>::class.java).toList()
        } catch (e: Exception) {
            emptyList()
        }
        
        return ChatSession(
            id = entity.id,
            modelId = entity.modelId,
            title = entity.title,
            taskType = taskType,
            messageCount = entity.messageCount,
            isBookmarked = entity.isBookmarked,
            tags = tags,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
    
    fun toEntity(domain: ChatSession): ChatSessionEntity {
        return ChatSessionEntity(
            id = domain.id,
            modelId = domain.modelId,
            title = domain.title,
            taskType = domain.taskType.name,
            messageCount = domain.messageCount,
            isBookmarked = domain.isBookmarked,
            tags = gson.toJson(domain.tags),
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }
    
    fun toDomain(entity: ChatMessageEntity): ChatMessage {
        val responseMetadata = entity.metadata?.let { metadataJson ->
            try {
                gson.fromJson(metadataJson, ResponseMetadata::class.java)
            } catch (e: Exception) {
                null
            }
        }
        
        return when (entity.sender) {
            ChatSender.USER -> ChatMessage.User(
                id = entity.id,
                sessionId = entity.sessionId,
                content = entity.content,
                timestamp = entity.timestamp,
                imageUri = entity.imageUri,
                audioUri = entity.audioUri
            )
            ChatSender.AI -> ChatMessage.Assistant(
                id = entity.id,
                sessionId = entity.sessionId,
                content = entity.content,
                timestamp = entity.timestamp,
                metadata = responseMetadata
            )
            else -> ChatMessage.User(
                id = entity.id,
                sessionId = entity.sessionId,
                content = entity.content,
                timestamp = entity.timestamp,
                imageUri = entity.imageUri,
                audioUri = entity.audioUri
            )
        }
    }
    
    fun toEntity(domain: ChatMessage): ChatMessageEntity {
        return when (domain) {
            is ChatMessage.User -> ChatMessageEntity(
                id = domain.id,
                sessionId = domain.sessionId,
                messageType = "TEXT",
                content = domain.content,
                sender = ChatSender.USER,
                timestamp = domain.timestamp,
                isStreaming = false,
                imageUri = domain.imageUri,
                audioUri = domain.audioUri,
                metadata = null,
                tokenCount = null,
                responseTimeMs = null
            )
            is ChatMessage.Assistant -> ChatMessageEntity(
                id = domain.id,
                sessionId = domain.sessionId,
                messageType = "TEXT",
                content = domain.content,
                sender = ChatSender.AI,
                timestamp = domain.timestamp,
                isStreaming = false,
                imageUri = null,
                audioUri = null,
                metadata = domain.metadata?.let { gson.toJson(it) },
                tokenCount = domain.metadata?.tokenCount,
                responseTimeMs = domain.metadata?.responseTimeMs?.toLong()
            )
        }
    }
    
    fun toDomainList(entities: List<ChatSessionEntity>): List<ChatSession> {
        return entities.map { toDomain(it) }
    }
    
    fun toEntityList(domains: List<ChatSession>): List<ChatSessionEntity> {
        return domains.map { toEntity(it) }
    }
    
    fun messagesToDomainList(entities: List<ChatMessageEntity>): List<ChatMessage> {
        return entities.map { toDomain(it) }
    }
    
    fun messagesToEntityList(domains: List<ChatMessage>): List<ChatMessageEntity> {
        return domains.map { toEntity(it) }
    }
}