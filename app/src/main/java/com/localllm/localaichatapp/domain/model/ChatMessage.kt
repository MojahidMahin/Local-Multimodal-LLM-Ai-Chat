package com.localllm.localaichatapp.domain.model

import java.util.UUID

sealed class ChatMessage {
    abstract val id: String
    abstract val timestamp: Long
    abstract val sender: ChatSender
    abstract val tokenCount: Int?
    abstract val responseTimeMs: Long?
}

data class TextMessage(
    override val id: String = UUID.randomUUID().toString(),
    override val timestamp: Long = System.currentTimeMillis(),
    override val sender: ChatSender,
    val content: String,
    val isStreaming: Boolean = false,
    override val tokenCount: Int? = null,
    override val responseTimeMs: Long? = null,
    val metadata: Map<String, Any>? = null
) : ChatMessage()

data class LoadingMessage(
    override val id: String = UUID.randomUUID().toString(),
    override val timestamp: Long = System.currentTimeMillis(),
    override val sender: ChatSender = ChatSender.AI,
    override val tokenCount: Int? = null,
    override val responseTimeMs: Long? = null
) : ChatMessage()

data class ErrorMessage(
    override val id: String = UUID.randomUUID().toString(),
    override val timestamp: Long = System.currentTimeMillis(),
    override val sender: ChatSender = ChatSender.SYSTEM,
    val error: String,
    override val tokenCount: Int? = null,
    override val responseTimeMs: Long? = null
) : ChatMessage()

data class ImageMessage(
    override val id: String = UUID.randomUUID().toString(),
    override val timestamp: Long = System.currentTimeMillis(),
    override val sender: ChatSender,
    val imageUri: String,
    val caption: String? = null,
    val analysisResult: String? = null,
    override val tokenCount: Int? = null,
    override val responseTimeMs: Long? = null
) : ChatMessage()

data class AudioMessage(
    override val id: String = UUID.randomUUID().toString(),
    override val timestamp: Long = System.currentTimeMillis(),
    override val sender: ChatSender,
    val audioUri: String,
    val duration: Long? = null,
    val transcription: String? = null,
    val analysisResult: String? = null,
    override val tokenCount: Int? = null,
    override val responseTimeMs: Long? = null
) : ChatMessage()

data class BenchmarkMessage(
    override val id: String = UUID.randomUUID().toString(),
    override val timestamp: Long = System.currentTimeMillis(),
    override val sender: ChatSender = ChatSender.SYSTEM,
    val benchmark: Benchmark,
    override val tokenCount: Int? = null,
    override val responseTimeMs: Long? = null
) : ChatMessage()