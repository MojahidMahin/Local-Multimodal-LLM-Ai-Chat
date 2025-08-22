package com.localllm.localaichatapp.presentation.askimage

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
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
import java.io.File
import java.util.*
import javax.inject.Inject

data class AskImageUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val selectedImageUri: Uri? = null,
    val isLoading: Boolean = false,
    val isStreaming: Boolean = false,
    val modelName: String = "",
    val sessionTitle: String = "",
    val currentSession: ChatSession? = null,
    val currentModel: Model? = null,
    val error: String? = null
)

@HiltViewModel
class AskImageViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val modelRepository: ModelRepository,
    private val aiInferenceRepository: AiInferenceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AskImageUiState())
    val uiState: StateFlow<AskImageUiState> = _uiState.asStateFlow()

    private var currentSessionId: String? = null
    private var pendingCameraUri: Uri? = null

    fun initializeSession(modelId: String) {
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

                // Create new session for image analysis
                val session = chatRepository.createSession(
                    modelId = modelId,
                    title = "Image Analysis",
                    taskType = TaskType.ASK_IMAGE
                )
                currentSessionId = session.id

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
                loadMessages(session.id)

            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to initialize session: ${e.message}"
                    )
                }
            }
        }
    }

    private fun loadMessages(sessionId: String) {
        viewModelScope.launch {
            chatRepository.observeMessages(sessionId)
                .collect { messages ->
                    _uiState.update { it.copy(messages = messages) }
                }
        }
    }

    fun updateInputText(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun selectImage(uri: Uri) {
        _uiState.update { it.copy(selectedImageUri = uri) }
    }

    fun clearSelectedImage() {
        _uiState.update { it.copy(selectedImageUri = null) }
    }

    fun createImageUri(context: Context): Uri {
        val imageFile = File(context.cacheDir, "camera_image_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
        pendingCameraUri = uri
        return uri
    }

    fun onCameraImageCaptured() {
        pendingCameraUri?.let { uri ->
            selectImage(uri)
            pendingCameraUri = null
        }
    }

    fun sendMessage() {
        val currentState = _uiState.value
        val inputText = currentState.inputText.trim()
        val imageUri = currentState.selectedImageUri
        val sessionId = currentSessionId

        if ((inputText.isBlank() && imageUri == null) || sessionId == null || currentState.isStreaming) {
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { 
                    it.copy(
                        inputText = "",
                        selectedImageUri = null,
                        isStreaming = true
                    )
                }

                // Create and save user message with image
                val userMessage = ChatMessage.User(
                    id = UUID.randomUUID().toString(),
                    sessionId = sessionId,
                    content = inputText.ifBlank { "Analyze this image" },
                    timestamp = System.currentTimeMillis(),
                    imageUri = imageUri?.toString()
                )

                chatRepository.addMessage(sessionId, userMessage)

                // Update session title if it's the first message
                if (currentState.messages.isEmpty()) {
                    val title = if (inputText.isNotBlank()) {
                        inputText.take(50).trim()
                    } else {
                        "Image Analysis"
                    }
                    
                    val updatedSession = currentState.currentSession?.copy(
                        title = title,
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

                // Generate AI response for image analysis
                generateImageAnalysis(sessionId, inputText, imageUri)

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

    private suspend fun generateImageAnalysis(sessionId: String, userInput: String, imageUri: Uri?) {
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

            chatRepository.addMessage(sessionId, initialAssistantMessage)

            // Generate response with image analysis
            val responseFlow = aiInferenceRepository.generateImageAnalysis(
                model = model,
                messages = conversationHistory,
                userInput = userInput.ifBlank { "Analyze this image" },
                imageUri = imageUri
            )

            var fullContent = ""
            responseFlow.collect { chunk ->
                fullContent += chunk.content
                
                // Update the assistant message with accumulated content
                val updatedMessage = initialAssistantMessage.copy(
                    content = fullContent,
                    metadata = chunk.metadata
                )
                
                chatRepository.updateMessage(updatedMessage.id, updatedMessage)
            }

            _uiState.update { it.copy(isStreaming = false) }

        } catch (e: Exception) {
            _uiState.update { 
                it.copy(
                    isStreaming = false,
                    error = "Failed to analyze image: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearSession() {
        viewModelScope.launch {
            currentSessionId?.let { sessionId ->
                try {
                    chatRepository.clearMessages(sessionId)
                    _uiState.update { 
                        it.copy(
                            messages = emptyList(),
                            sessionTitle = "Image Analysis"
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update { 
                        it.copy(error = "Failed to clear session: ${e.message}")
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