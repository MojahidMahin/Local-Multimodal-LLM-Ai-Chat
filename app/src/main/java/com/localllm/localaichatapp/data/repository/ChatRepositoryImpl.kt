package com.localllm.localaichatapp.data.repository

import com.localllm.localaichatapp.data.local.database.dao.ChatDao
import com.localllm.localaichatapp.data.mapper.ChatMapper
import com.localllm.localaichatapp.domain.model.ChatMessage
import com.localllm.localaichatapp.domain.model.ChatSession
import com.localllm.localaichatapp.domain.model.TaskType
import com.localllm.localaichatapp.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao,
    private val chatMapper: ChatMapper
) : ChatRepository {
    
    override suspend fun createSession(modelId: String, title: String, taskType: TaskType): ChatSession {
        val session = ChatSession(
            id = UUID.randomUUID().toString(),
            title = title,
            modelId = modelId,
            taskType = taskType,
            messages = emptyList(),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        
        chatDao.insertSession(chatMapper.toEntity(session))
        return session
    }
    
    override suspend fun getSession(sessionId: String): ChatSession? {
        val sessionEntity = chatDao.getSession(sessionId) ?: return null
        return chatMapper.toDomain(sessionEntity)
    }
    
    override fun getAllSessions(): Flow<List<ChatSession>> {
        return chatDao.getAllSessions().map { sessionEntities ->
            chatMapper.toDomainList(sessionEntities)
        }
    }
    
    override fun getSessionsByTaskType(taskType: TaskType): Flow<List<ChatSession>> {
        return chatDao.getSessionsByTaskType(taskType.name).map { sessionEntities ->
            chatMapper.toDomainList(sessionEntities)
        }
    }
    
    override fun getSessionsByModel(modelId: String): Flow<List<ChatSession>> {
        return chatDao.getSessionsByModel(modelId).map { sessionEntities ->
            chatMapper.toDomainList(sessionEntities)
        }
    }
    
    override fun getBookmarkedSessions(): Flow<List<ChatSession>> {
        return chatDao.getBookmarkedSessions().map { sessionEntities ->
            chatMapper.toDomainList(sessionEntities)
        }
    }
    
    override fun searchSessions(query: String): Flow<List<ChatSession>> {
        return chatDao.searchSessions("%$query%").map { sessionEntities ->
            chatMapper.toDomainList(sessionEntities)
        }
    }
    
    override suspend fun addMessage(sessionId: String, message: ChatMessage): Result<Unit> {
        return try {
            chatDao.insertMessage(chatMapper.toEntity(message))
            
            // Update session's updatedAt timestamp
            val session = chatDao.getSession(sessionId)
            session?.let {
                chatDao.updateSession(
                    it.copy(updatedAt = System.currentTimeMillis())
                )
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateMessage(messageId: String, updatedMessage: ChatMessage): Result<Unit> {
        return try {
            chatDao.updateMessage(chatMapper.toEntity(updatedMessage))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateSession(session: ChatSession): Result<Unit> {
        return try {
            chatDao.updateSession(chatMapper.toEntity(session))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateBookmarkStatus(sessionId: String, isBookmarked: Boolean): Result<Unit> {
        return try {
            chatDao.updateBookmarkStatus(sessionId, isBookmarked)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteSession(sessionId: String): Result<Unit> {
        return try {
            chatDao.deleteSession(sessionId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteMessage(messageId: String): Result<Unit> {
        return try {
            chatDao.deleteMessage(messageId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun clearMessages(sessionId: String): Result<Unit> {
        return try {
            chatDao.clearMessagesForSession(sessionId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun observeSession(sessionId: String): Flow<ChatSession?> {
        return chatDao.observeSession(sessionId).map { sessionEntity ->
            sessionEntity?.let { chatMapper.toDomain(it) }
        }
    }
    
    override fun observeMessages(sessionId: String): Flow<List<ChatMessage>> {
        return chatDao.getMessagesForSession(sessionId).map { messageEntities ->
            chatMapper.messagesToDomainList(messageEntities)
        }
    }
    
    override fun getMessagesByType(sessionId: String, messageType: String): Flow<List<ChatMessage>> {
        return chatDao.getMessagesByType(sessionId, messageType).map { messageEntities ->
            chatMapper.messagesToDomainList(messageEntities)
        }
    }
    
    override fun getAllImageMessages(): Flow<List<ChatMessage>> {
        return chatDao.getAllImageMessages().map { messageEntities ->
            chatMapper.messagesToDomainList(messageEntities)
        }
    }
    
    override fun getAllAudioMessages(): Flow<List<ChatMessage>> {
        return chatDao.getAllAudioMessages().map { messageEntities ->
            chatMapper.messagesToDomainList(messageEntities)
        }
    }
    
    override suspend fun getMessageCount(sessionId: String): Int {
        return chatDao.getMessageCount(sessionId)
    }
    
    override suspend fun deleteOldMessages(cutoffTime: Long) {
        chatDao.deleteOldMessages(cutoffTime)
    }
}