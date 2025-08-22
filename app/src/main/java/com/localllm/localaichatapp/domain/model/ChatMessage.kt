package com.localllm.localaichatapp.domain.model

sealed class ChatMessage {
    abstract val id: String
    abstract val sessionId: String
    abstract val content: String
    abstract val timestamp: Long

    data class User(
        override val id: String,
        override val sessionId: String,
        override val content: String,
        override val timestamp: Long,
        val imageUri: String? = null,
        val audioUri: String? = null
    ) : ChatMessage()

    data class Assistant(
        override val id: String,
        override val sessionId: String,
        override val content: String,
        override val timestamp: Long,
        val metadata: ResponseMetadata? = null
    ) : ChatMessage()
}