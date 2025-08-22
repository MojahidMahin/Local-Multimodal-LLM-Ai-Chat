package com.localllm.localaichatapp.data.local.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.localllm.localaichatapp.data.local.database.entity.ChatSessionEntity
import com.localllm.localaichatapp.data.local.database.entity.ChatMessageEntity

@Dao
interface ChatDao {
    // Session operations
    @Query("SELECT * FROM chat_sessions ORDER BY updatedAt DESC")
    fun getAllSessions(): Flow<List<ChatSessionEntity>>
    
    @Query("SELECT * FROM chat_sessions WHERE taskType = :taskType ORDER BY updatedAt DESC")
    fun getSessionsByTaskType(taskType: String): Flow<List<ChatSessionEntity>>
    
    @Query("SELECT * FROM chat_sessions WHERE modelId = :modelId ORDER BY updatedAt DESC")
    fun getSessionsByModel(modelId: String): Flow<List<ChatSessionEntity>>
    
    @Query("SELECT * FROM chat_sessions WHERE isBookmarked = 1 ORDER BY updatedAt DESC")
    fun getBookmarkedSessions(): Flow<List<ChatSessionEntity>>
    
    @Query("SELECT * FROM chat_sessions WHERE title LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    fun searchSessions(query: String): Flow<List<ChatSessionEntity>>
    
    @Query("SELECT * FROM chat_sessions WHERE id = :sessionId")
    suspend fun getSession(sessionId: String): ChatSessionEntity?
    
    @Query("SELECT * FROM chat_sessions WHERE id = :sessionId")
    fun observeSession(sessionId: String): Flow<ChatSessionEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ChatSessionEntity)
    
    @Update
    suspend fun updateSession(session: ChatSessionEntity)
    
    @Query("UPDATE chat_sessions SET messageCount = messageCount + 1, updatedAt = :updatedAt WHERE id = :sessionId")
    suspend fun incrementMessageCount(sessionId: String, updatedAt: Long = System.currentTimeMillis())
    
    @Query("UPDATE chat_sessions SET isBookmarked = :isBookmarked, updatedAt = :updatedAt WHERE id = :sessionId")
    suspend fun updateBookmarkStatus(sessionId: String, isBookmarked: Boolean, updatedAt: Long = System.currentTimeMillis())
    
    @Query("DELETE FROM chat_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: String)
    
    // Message operations
    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getMessagesForSession(sessionId: String): Flow<List<ChatMessageEntity>>
    
    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId AND messageType = :messageType ORDER BY timestamp ASC")
    fun getMessagesByType(sessionId: String, messageType: String): Flow<List<ChatMessageEntity>>
    
    @Query("SELECT * FROM chat_messages WHERE messageType = 'IMAGE' OR imageUri IS NOT NULL ORDER BY timestamp DESC")
    fun getAllImageMessages(): Flow<List<ChatMessageEntity>>
    
    @Query("SELECT * FROM chat_messages WHERE messageType = 'AUDIO' OR audioUri IS NOT NULL ORDER BY timestamp DESC")
    fun getAllAudioMessages(): Flow<List<ChatMessageEntity>>
    
    @Query("SELECT * FROM chat_messages WHERE id = :messageId")
    suspend fun getMessage(messageId: String): ChatMessageEntity?
    
    @Query("SELECT COUNT(*) FROM chat_messages WHERE sessionId = :sessionId")
    suspend fun getMessageCount(sessionId: String): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessageEntity>)
    
    @Update
    suspend fun updateMessage(message: ChatMessageEntity)
    
    @Query("UPDATE chat_messages SET content = :content, isStreaming = :isStreaming WHERE id = :messageId")
    suspend fun updateMessageContent(messageId: String, content: String, isStreaming: Boolean)
    
    @Query("DELETE FROM chat_messages WHERE id = :messageId")
    suspend fun deleteMessage(messageId: String)
    
    @Query("DELETE FROM chat_messages WHERE sessionId = :sessionId")
    suspend fun clearMessagesForSession(sessionId: String)
    
    @Query("DELETE FROM chat_messages WHERE timestamp < :cutoffTime")
    suspend fun deleteOldMessages(cutoffTime: Long)
    
    @Transaction
    suspend fun insertSessionWithMessages(
        session: ChatSessionEntity,
        messages: List<ChatMessageEntity>
    ) {
        insertSession(session)
        insertMessages(messages)
    }
    
    @Transaction
    suspend fun addMessageAndUpdateSession(
        message: ChatMessageEntity,
        sessionId: String
    ) {
        insertMessage(message)
        incrementMessageCount(sessionId)
    }
}