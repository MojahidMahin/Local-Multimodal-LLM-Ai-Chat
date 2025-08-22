package com.localllm.localaichatapp.presentation.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localllm.localaichatapp.domain.model.ModelConfiguration
import com.localllm.localaichatapp.domain.usecase.ManageModelsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModelManagementViewModel @Inject constructor(
    private val manageModelsUseCase: ManageModelsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ModelManagementUiState())
    val uiState = _uiState.asStateFlow()
    
    init {
        loadModels()
    }
    
    fun onEvent(event: ModelManagementEvent) {
        when (event) {
            is ModelManagementEvent.DownloadModel -> downloadModel(event.modelId)
            is ModelManagementEvent.DeleteModel -> deleteModel(event.modelId)
            is ModelManagementEvent.InitializeModel -> initializeModel(event.modelId)
            is ModelManagementEvent.ShowDeleteConfirmation -> showDeleteConfirmation(event.model)
            is ModelManagementEvent.HideDeleteConfirmation -> hideDeleteConfirmation()
            is ModelManagementEvent.ConfirmDeletion -> confirmDeletion()
            is ModelManagementEvent.ClearError -> clearError()
            is ModelManagementEvent.RefreshModels -> loadModels()
        }
    }
    
    private fun loadModels() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        
        viewModelScope.launch {
            manageModelsUseCase.getAvailableModels()
                .catch { throwable ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to load models: ${throwable.message}"
                    )
                }
                .collect { models ->
                    _uiState.value = _uiState.value.copy(
                        availableModels = models,
                        isLoading = false
                    )
                }
        }
    }
    
    private fun downloadModel(modelId: String) {
        viewModelScope.launch {
            try {
                val currentDownloading = _uiState.value.downloadingModels.toMutableMap()
                currentDownloading[modelId] = 0f
                _uiState.value = _uiState.value.copy(
                    downloadingModels = currentDownloading,
                    error = null
                )
                
                manageModelsUseCase.downloadModel(modelId)
                    .onEach { progress ->
                        val updatedDownloading = _uiState.value.downloadingModels.toMutableMap()
                        updatedDownloading[modelId] = progress
                        _uiState.value = _uiState.value.copy(
                            downloadingModels = updatedDownloading
                        )
                    }
                    .catch { throwable ->
                        val updatedDownloading = _uiState.value.downloadingModels.toMutableMap()
                        updatedDownloading.remove(modelId)
                        _uiState.value = _uiState.value.copy(
                            downloadingModels = updatedDownloading,
                            error = "Download failed: ${throwable.message}"
                        )
                    }
                    .launchIn(this)
                    
                // Remove from downloading list when complete
                val updatedDownloading = _uiState.value.downloadingModels.toMutableMap()
                updatedDownloading.remove(modelId)
                _uiState.value = _uiState.value.copy(
                    downloadingModels = updatedDownloading
                )
                
            } catch (e: Exception) {
                val updatedDownloading = _uiState.value.downloadingModels.toMutableMap()
                updatedDownloading.remove(modelId)
                _uiState.value = _uiState.value.copy(
                    downloadingModels = updatedDownloading,
                    error = "Failed to start download: ${e.message}"
                )
            }
        }
    }
    
    private fun deleteModel(modelId: String) {
        viewModelScope.launch {
            try {
                manageModelsUseCase.deleteModel(modelId).getOrThrow()
                _uiState.value = _uiState.value.copy(error = null)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to delete model: ${e.message}"
                )
            }
        }
    }
    
    private fun initializeModel(modelId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(error = null)
                
                if (!manageModelsUseCase.isModelReady(modelId)) {
                    manageModelsUseCase.initializeModel(
                        modelId = modelId,
                        config = ModelConfiguration()
                    ).getOrThrow()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to initialize model: ${e.message}"
                )
            }
        }
    }
    
    private fun showDeleteConfirmation(model: com.localllm.localaichatapp.domain.model.AiModel) {
        _uiState.value = _uiState.value.copy(
            selectedModelForDeletion = model,
            showDeleteConfirmation = true
        )
    }
    
    private fun hideDeleteConfirmation() {
        _uiState.value = _uiState.value.copy(
            selectedModelForDeletion = null,
            showDeleteConfirmation = false
        )
    }
    
    private fun confirmDeletion() {
        val modelToDelete = _uiState.value.selectedModelForDeletion
        if (modelToDelete != null) {
            deleteModel(modelToDelete.id)
            hideDeleteConfirmation()
        }
    }
    
    private fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}