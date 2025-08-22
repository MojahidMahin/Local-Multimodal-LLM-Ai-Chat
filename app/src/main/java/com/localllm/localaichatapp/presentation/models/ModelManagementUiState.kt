package com.localllm.localaichatapp.presentation.models

import com.localllm.localaichatapp.domain.model.Model

data class ModelManagementUiState(
    val availableModels: List<Model> = emptyList(),
    val downloadingModels: Map<String, Float> = emptyMap(), // modelId to progress
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedModelForDeletion: Model? = null,
    val showDeleteConfirmation: Boolean = false
)

sealed class ModelManagementEvent {
    data class DownloadModel(val modelId: String) : ModelManagementEvent()
    data class DeleteModel(val modelId: String) : ModelManagementEvent()
    data class InitializeModel(val modelId: String) : ModelManagementEvent()
    data class ShowDeleteConfirmation(val model: Model) : ModelManagementEvent()
    object HideDeleteConfirmation : ModelManagementEvent()
    object ConfirmDeletion : ModelManagementEvent()
    object ClearError : ModelManagementEvent()
    object RefreshModels : ModelManagementEvent()
}