package com.localllm.localaichatapp.domain.usecase

import com.localllm.localaichatapp.domain.model.ChatSession
import com.localllm.localaichatapp.domain.model.TaskType
import com.localllm.localaichatapp.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ManageSessionsUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend fun createSession(
        modelId: String, 
        title: String = "New Chat", 
        taskType: TaskType = TaskType.CHAT
    ): ChatSession {
        return chatRepository.createSession(modelId, title, taskType)
    }
    
    fun getAllSessions(): Flow<List<ChatSession>> {
        return chatRepository.getAllSessions()
    }
    
    suspend fun getSession(sessionId: String): ChatSession? {
        return chatRepository.getSession(sessionId)
    }
    
    fun observeSession(sessionId: String): Flow<ChatSession?> {
        return chatRepository.observeSession(sessionId)
    }
    
    suspend fun deleteSession(sessionId: String): Result<Unit> {
        return chatRepository.deleteSession(sessionId)
    }
    
    suspend fun clearMessages(sessionId: String): Result<Unit> {
        return chatRepository.clearMessages(sessionId)
    }
}