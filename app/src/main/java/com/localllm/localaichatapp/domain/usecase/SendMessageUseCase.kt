package com.localllm.localaichatapp.domain.usecase

import com.localllm.localaichatapp.domain.model.ChatMessage
import com.localllm.localaichatapp.domain.model.ChatSender
import com.localllm.localaichatapp.domain.model.LoadingMessage
import com.localllm.localaichatapp.domain.model.StreamingResponse
import com.localllm.localaichatapp.domain.model.TextMessage
import com.localllm.localaichatapp.domain.repository.AiInferenceRepository
import com.localllm.localaichatapp.domain.repository.ChatRepository
import com.localllm.localaichatapp.domain.repository.ModelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    private val aiInferenceRepository: AiInferenceRepository,
    private val modelRepository: ModelRepository
) {
    suspend operator fun invoke(
        sessionId: String,
        message: String,
        imageUri: String? = null
    ): Flow<StreamingResponse> = flow {
        try {
            val session = chatRepository.getSession(sessionId)
                ?: throw IllegalArgumentException("Session not found")
            
            val userMessage = TextMessage(
                content = message,
                sender = ChatSender.USER
            )
            
            chatRepository.addMessage(sessionId, userMessage)
            
            val loadingMessage = LoadingMessage()
            chatRepository.addMessage(sessionId, loadingMessage)
            
            if (!modelRepository.isModelReady(session.modelId)) {
                throw IllegalStateException("Model ${session.modelId} is not ready")
            }
            
            val conversationHistory = session.messages
                .filterIsInstance<TextMessage>()
                .map { "${it.sender.name}: ${it.content}" }
            
            val responseFlow = if (imageUri != null) {
                aiInferenceRepository.generateResponseWithImage(
                    session.modelId,
                    message,
                    imageUri,
                    conversationHistory
                )
            } else {
                aiInferenceRepository.generateResponse(
                    session.modelId,
                    message,
                    conversationHistory
                )
            }
            
            var currentResponse = ""
            var aiMessageId: String? = null
            
            responseFlow.collect { streamingResponse ->
                currentResponse += streamingResponse.content
                
                if (aiMessageId == null) {
                    val aiMessage = TextMessage(
                        content = currentResponse,
                        sender = ChatSender.AI,
                        isStreaming = !streamingResponse.isComplete
                    )
                    aiMessageId = aiMessage.id
                    chatRepository.addMessage(sessionId, aiMessage)
                } else {
                    val updatedMessage = TextMessage(
                        id = aiMessageId!!,
                        content = currentResponse,
                        sender = ChatSender.AI,
                        isStreaming = !streamingResponse.isComplete
                    )
                    chatRepository.updateMessage(sessionId, aiMessageId!!, updatedMessage)
                }
                
                emit(streamingResponse)
            }
            
        } catch (e: Exception) {
            val errorMessage = TextMessage(
                content = "Error: ${e.message}",
                sender = ChatSender.SYSTEM
            )
            chatRepository.addMessage(sessionId, errorMessage)
            throw e
        }
    }
}