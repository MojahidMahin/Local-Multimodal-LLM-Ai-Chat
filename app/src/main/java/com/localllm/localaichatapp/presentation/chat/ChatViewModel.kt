package com.localllm.localaichatapp.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localllm.localaichatapp.domain.model.ChatMessage
import com.localllm.localaichatapp.domain.model.ChatSession
import com.localllm.localaichatapp.domain.model.Model
import com.localllm.localaichatapp.domain.model.TaskType
import com.localllm.localaichatapp.domain.repository.AiInferenceRepository
import com.localllm.localaichatapp.domain.repository.ChatRepository
import com.localllm.localaichatapp.domain.repository.ModelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val isStreaming: Boolean = false,
    val modelName: String = "",
    val sessionTitle: String = "",
    val currentSession: ChatSession? = null,
    val currentModel: Model? = null,
    val error: String? = null
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val modelRepository: ModelRepository,
    private val aiInferenceRepository: AiInferenceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var currentSessionId: String? = null

    fun initializeChat(modelId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // Get model information
                val model = modelRepository.getModelById(modelId).first()
                if (model == null) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Model not found"
                        )
                    }
                    return@launch
                }

                // Create new chat session
                val sessionId = UUID.randomUUID().toString()
                val session = ChatSession(
                    id = sessionId,
                    title = "New Chat", // Will be updated with first message
                    taskType = TaskType.CHAT,
                    modelId = modelId,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis(),
                    isBookmarked = false,
                    tags = emptyList()
                )

                chatRepository.createSession(session)
                currentSessionId = sessionId

                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        currentSession = session,
                        currentModel = model,
                        modelName = model.name,
                        sessionTitle = session.title
                    )
                }

                // Load existing messages if any
                loadMessages(sessionId)

            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to initialize chat: ${e.message}"
                    )
                }
            }
        }
    }

    private suspend fun loadMessages(sessionId: String) {
        chatRepository.getMessagesForSession(sessionId)
            .collect { messages ->
                _uiState.update { it.copy(messages = messages) }
            }
    }

    fun updateInputText(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun sendMessage() {
        val currentState = _uiState.value
        val inputText = currentState.inputText.trim()
        val sessionId = currentSessionId

        if (inputText.isBlank() || sessionId == null || currentState.isStreaming) {
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { 
                    it.copy(
                        inputText = "",
                        isStreaming = true
                    )
                }

                // Create and save user message
                val userMessage = ChatMessage.User(
                    id = UUID.randomUUID().toString(),
                    sessionId = sessionId,
                    content = inputText,
                    timestamp = System.currentTimeMillis()
                )

                chatRepository.addMessage(userMessage)

                // Update session title if it's the first message
                if (currentState.messages.isEmpty()) {
                    val updatedSession = currentState.currentSession?.copy(
                        title = inputText.take(50).trim(),
                        updatedAt = System.currentTimeMillis()
                    )
                    updatedSession?.let { 
                        chatRepository.updateSession(it)
                        _uiState.update { state -> 
                            state.copy(
                                currentSession = updatedSession,
                                sessionTitle = updatedSession.title
                            )
                        }
                    }
                }

                // Generate AI response
                generateAiResponse(sessionId, inputText)

            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isStreaming = false,
                        error = "Failed to send message: ${e.message}"
                    )
                }
            }
        }
    }

    private suspend fun generateAiResponse(sessionId: String, userInput: String) {
        try {
            val model = _uiState.value.currentModel ?: return
            val conversationHistory = _uiState.value.messages

            // Create initial assistant message
            val assistantMessageId = UUID.randomUUID().toString()
            val initialAssistantMessage = ChatMessage.Assistant(
                id = assistantMessageId,
                sessionId = sessionId,
                content = "",
                timestamp = System.currentTimeMillis(),
                metadata = null
            )

            chatRepository.addMessage(initialAssistantMessage)

            // Generate response with streaming
            val responseFlow = aiInferenceRepository.generateChatResponse(
                model = model,
                messages = conversationHistory,
                userInput = userInput
            )

            var fullContent = ""
            responseFlow.collect { chunk ->
                fullContent += chunk.text
                
                // Update the assistant message with accumulated content
                val updatedMessage = initialAssistantMessage.copy(
                    content = fullContent,
                    metadata = chunk.metadata
                )
                
                chatRepository.updateMessage(updatedMessage)
            }

            _uiState.update { it.copy(isStreaming = false) }

        } catch (e: Exception) {
            _uiState.update { 
                it.copy(
                    isStreaming = false,
                    error = "Failed to generate response: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearChat() {
        viewModelScope.launch {
            currentSessionId?.let { sessionId ->
                try {
                    chatRepository.clearMessagesForSession(sessionId)
                    _uiState.update { 
                        it.copy(
                            messages = emptyList(),
                            sessionTitle = "New Chat"
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update { 
                        it.copy(error = "Failed to clear chat: ${e.message}")
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Save session state before clearing
        currentSessionId?.let { sessionId ->
            viewModelScope.launch {
                try {
                    val currentSession = _uiState.value.currentSession?.copy(
                        updatedAt = System.currentTimeMillis()
                    )
                    currentSession?.let { 
                        chatRepository.updateSession(it)
                    }
                } catch (e: Exception) {
                    // Log error but don't crash
                }
            }
        }
    }
}