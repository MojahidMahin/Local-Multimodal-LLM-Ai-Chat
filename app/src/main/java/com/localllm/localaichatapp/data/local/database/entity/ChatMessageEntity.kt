package com.localllm.localaichatapp.data.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.localllm.localaichatapp.domain.model.ChatSender

@Entity(
    tableName = "chat_messages",
    foreignKeys = [
        ForeignKey(
            entity = ChatSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sessionId"]), Index(value = ["timestamp"])]
)
data class ChatMessageEntity(
    @PrimaryKey
    val id: String,
    val sessionId: String,
    val content: String,
    val sender: ChatSender,
    val timestamp: Long,
    val messageType: String, // TEXT, LOADING, ERROR, IMAGE, AUDIO, BENCHMARK
    val imageUri: String? = null,
    val audioUri: String? = null,
    val metadata: String? = null, // JSON string for additional data (benchmarks, configs, etc.)
    val isStreaming: Boolean = false,
    val tokenCount: Int? = null,
    val responseTimeMs: Long? = null
)