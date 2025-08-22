package com.localllm.localaichatapp.domain.usecase

import com.localllm.localaichatapp.domain.model.ChatMessage
import com.localllm.localaichatapp.domain.model.StreamingResponse
import com.localllm.localaichatapp.domain.model.TaskType
import com.localllm.localaichatapp.domain.repository.AiInferenceRepository
import com.localllm.localaichatapp.domain.repository.ChatRepository
import com.localllm.localaichatapp.domain.repository.ModelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    private val aiInferenceRepository: AiInferenceRepository,
    private val modelRepository: ModelRepository
) {
    suspend operator fun invoke(
        sessionId: String,
        message: String,
        imageUri: String? = null,
        audioUri: String? = null
    ): Flow<StreamingResponse> = flow {
        try {
            val session = chatRepository.getSession(sessionId)
                ?: throw IllegalArgumentException("Session not found")
            
            // Create user message
            val userMessage = ChatMessage.User(
                id = UUID.randomUUID().toString(),
                sessionId = sessionId,
                content = message,
                timestamp = System.currentTimeMillis(),
                imageUri = imageUri,
                audioUri = audioUri
            )
            
            // Add user message to repository
            chatRepository.addMessage(sessionId, userMessage)
            
            // Check if model is ready
            if (!modelRepository.isModelReady(session.modelId)) {
                throw IllegalStateException("Model ${session.modelId} is not ready")
            }
            
            // Get conversation history
            val messages = chatRepository.observeMessages(sessionId)
            val conversationHistory = session.messages.map { it.content }
            
            // Generate AI response based on input type
            val responseFlow = when {
                imageUri != null -> {
                    aiInferenceRepository.generateResponseWithImage(
                        session.modelId,
                        message,
                        imageUri,
                        session.taskType,
                        conversationHistory
                    )
                }
                audioUri != null -> {
                    aiInferenceRepository.generateResponseWithAudio(
                        session.modelId,
                        message,
                        audioUri,
                        session.taskType,
                        conversationHistory
                    )
                }
                else -> {
                    aiInferenceRepository.generateResponse(
                        session.modelId,
                        message,
                        session.taskType,
                        conversationHistory
                    )
                }
            }
            
            var currentResponse = ""
            var aiMessageId: String? = null
            
            // Process streaming response
            responseFlow.collect { streamingResponse ->
                currentResponse += streamingResponse.content
                
                if (aiMessageId == null && streamingResponse.content.isNotEmpty()) {
                    // Create new AI message
                    val aiMessage = ChatMessage.Assistant(
                        id = UUID.randomUUID().toString(),
                        sessionId = sessionId,
                        content = currentResponse,
                        timestamp = System.currentTimeMillis(),
                        metadata = streamingResponse.metadata
                    )
                    aiMessageId = aiMessage.id
                    chatRepository.addMessage(sessionId, aiMessage)
                } else if (aiMessageId != null) {
                    // Update existing AI message
                    val updatedMessage = ChatMessage.Assistant(
                        id = aiMessageId!!,
                        sessionId = sessionId,
                        content = currentResponse,
                        timestamp = System.currentTimeMillis(),
                        metadata = streamingResponse.metadata
                    )
                    chatRepository.updateMessage(aiMessageId!!, updatedMessage)
                }
                
                emit(streamingResponse)
            }
            
        } catch (e: Exception) {
            // Create error message
            val errorMessage = ChatMessage.Assistant(
                id = UUID.randomUUID().toString(),
                sessionId = sessionId,
                content = "Error: ${e.message}",
                timestamp = System.currentTimeMillis(),
                metadata = null
            )
            chatRepository.addMessage(sessionId, errorMessage)
            throw e
        }
    }
}