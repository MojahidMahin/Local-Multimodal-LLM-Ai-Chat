package com.localllm.localaichatapp.data.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chat_sessions",
    foreignKeys = [
        ForeignKey(
            entity = ModelEntity::class,
            parentColumns = ["id"],
            childColumns = ["modelId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["modelId"])]
)
data class ChatSessionEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val modelId: String,
    val taskType: String, // CHAT, ASK_IMAGE, ASK_AUDIO, PROMPT_LAB
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val messageCount: Int = 0,
    val isBookmarked: Boolean = false,
    val tags: String = "[]"
)