package com.localllm.localaichatapp.domain.model

import kotlinx.coroutines.flow.Flow

data class ChatSession(
    val id: String,
    val title: String,
    val modelId: String,
    val taskType: TaskType,
    val messages: List<ChatMessage> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val messageCount: Int = 0,
    val isBookmarked: Boolean = false,
    val tags: List<String> = emptyList()
) {
    val isEmpty: Boolean
        get() = messages.isEmpty()
    
    val lastMessage: ChatMessage?
        get() = messages.lastOrNull()
    
    val preview: String
        get() = when (val last = lastMessage) {
            is ChatMessage.User -> {
                when {
                    last.imageUri != null -> "📸 Image: ${last.content.take(50)}"
                    last.audioUri != null -> "🎵 Audio: ${last.content.take(50)}"
                    else -> last.content.take(100)
                }
            }
            is ChatMessage.Assistant -> last.content.take(100)
            else -> "Empty conversation"
        }
}

data class StreamingResponse(
    val content: String,
    val isComplete: Boolean,
    val metadata: ResponseMetadata? = null
)