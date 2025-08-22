package com.localllm.localaichatapp.presentation.chat

import com.localllm.localaichatapp.domain.model.Model
import com.localllm.localaichatapp.domain.model.ChatMessage
import com.localllm.localaichatapp.domain.model.ChatSession

data class ChatUiState(
    val currentSession: ChatSession? = null,
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val isModelInitializing: Boolean = false,
    val isGenerating: Boolean = false,
    val isStreaming: Boolean = false,
    val currentModel: Model? = null,
    val modelName: String = "",
    val sessionTitle: String = "",
    val inputText: String = "",
    val error: String? = null,
    val showModelPicker: Boolean = false
)

sealed class ChatUiEvent {
    data class SendMessage(val message: String, val imageUri: String? = null) : ChatUiEvent()
    object StopGeneration : ChatUiEvent()
    object ClearChat : ChatUiEvent()
    object RetryLastMessage : ChatUiEvent()
    data class UpdateInputText(val text: String) : ChatUiEvent()
    object ShowModelPicker : ChatUiEvent()
    object HideModelPicker : ChatUiEvent()
    data class SelectModel(val modelId: String) : ChatUiEvent()
    object ClearError : ChatUiEvent()
}