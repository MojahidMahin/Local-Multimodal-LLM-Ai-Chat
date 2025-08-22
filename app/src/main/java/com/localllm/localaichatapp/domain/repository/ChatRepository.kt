package com.localllm.localaichatapp.domain.repository

import com.localllm.localaichatapp.domain.model.ChatMessage
import com.localllm.localaichatapp.domain.model.ChatSession
import com.localllm.localaichatapp.domain.model.TaskType
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun createSession(modelId: String, title: String, taskType: TaskType): ChatSession
    suspend fun getSession(sessionId: String): ChatSession?
    fun getAllSessions(): Flow<List<ChatSession>>
    fun getSessionsByTaskType(taskType: TaskType): Flow<List<ChatSession>>
    fun getSessionsByModel(modelId: String): Flow<List<ChatSession>>
    fun getBookmarkedSessions(): Flow<List<ChatSession>>
    fun searchSessions(query: String): Flow<List<ChatSession>>
    suspend fun addMessage(sessionId: String, message: ChatMessage): Result<Unit>
    suspend fun updateMessage(messageId: String, updatedMessage: ChatMessage): Result<Unit>
    suspend fun updateSession(session: ChatSession): Result<Unit>
    suspend fun updateBookmarkStatus(sessionId: String, isBookmarked: Boolean): Result<Unit>
    suspend fun deleteSession(sessionId: String): Result<Unit>
    suspend fun deleteMessage(messageId: String): Result<Unit>
    suspend fun clearMessages(sessionId: String): Result<Unit>
    fun observeSession(sessionId: String): Flow<ChatSession?>
    fun observeMessages(sessionId: String): Flow<List<ChatMessage>>
    fun getMessagesByType(sessionId: String, messageType: String): Flow<List<ChatMessage>>
    fun getAllImageMessages(): Flow<List<ChatMessage>>
    fun getAllAudioMessages(): Flow<List<ChatMessage>>
    suspend fun getMessageCount(sessionId: String): Int
    suspend fun deleteOldMessages(cutoffTime: Long)
}