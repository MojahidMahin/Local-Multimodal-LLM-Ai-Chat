package com.localllm.localaichatapp.presentation.modelmanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localllm.localaichatapp.domain.model.Model
import com.localllm.localaichatapp.domain.model.ModelParameters
import com.localllm.localaichatapp.domain.model.ModelStatus
import com.localllm.localaichatapp.domain.model.TaskType
import com.localllm.localaichatapp.domain.repository.ModelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ModelManagerUiState(
    val models: List<Model> = emptyList(),
    val filteredModels: List<Model> = emptyList(),
    val selectedFilter: ModelFilter = ModelFilter.ALL,
    val isLoading: Boolean = false,
    val totalStorage: Long = 64L * 1024 * 1024 * 1024, // 64GB mock
    val usedStorage: Long = 0L,
    val showAddModelDialog: Boolean = false,
    val modelToDelete: Model? = null,
    val downloadProgress: Map<String, Float> = emptyMap(),
    val error: String? = null
)

@HiltViewModel
class ModelManagerViewModel @Inject constructor(
    private val modelRepository: ModelRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ModelManagerUiState())
    val uiState: StateFlow<ModelManagerUiState> = _uiState.asStateFlow()

    fun loadModels() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                modelRepository.getAllModels().collect { models ->
                    val usedStorage = models
                        .filter { it.status == ModelStatus.DOWNLOADED }
                        .sumOf { it.size }
                    
                    val filteredModels = filterModels(models, _uiState.value.selectedFilter)
                    
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            models = models,
                            filteredModels = filteredModels,
                            usedStorage = usedStorage,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to load models: ${e.message}"
                    )
                }
            }
        }
    }

    fun refreshModels() {
        viewModelScope.launch {
            try {
                // Simulate refresh from remote source
                _uiState.update { it.copy(isLoading = true) }
                
                // In real implementation, would fetch from server
                kotlinx.coroutines.delay(1000)
                
                loadModels()
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to refresh models: ${e.message}"
                    )
                }
            }
        }
    }

    fun selectFilter(filter: ModelFilter) {
        val currentState = _uiState.value
        val filteredModels = filterModels(currentState.models, filter)
        
        _uiState.update { 
            it.copy(
                selectedFilter = filter,
                filteredModels = filteredModels
            )
        }
    }

    fun downloadModel(modelId: String) {
        viewModelScope.launch {
            try {
                val models = _uiState.value.models.toMutableList()
                val modelIndex = models.indexOfFirst { it.id == modelId }
                
                if (modelIndex != -1) {
                    val model = models[modelIndex]
                    models[modelIndex] = model.copy(
                        status = ModelStatus.DOWNLOADING,
                        downloadProgress = 0f
                    )
                    
                    _uiState.update { state ->
                        state.copy(
                            models = models,
                            filteredModels = filterModels(models, state.selectedFilter)
                        )
                    }
                    
                    // Simulate download progress
                    simulateDownload(modelId)
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = "Failed to start download: ${e.message}")
                }
            }
        }
    }

    fun pauseDownload(modelId: String) {
        viewModelScope.launch {
            try {
                updateModelStatus(modelId, ModelStatus.PAUSED)
                _uiState.update { 
                    it.copy(error = "Download paused")
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = "Failed to pause download: ${e.message}")
                }
            }
        }
    }

    fun resumeDownload(modelId: String) {
        viewModelScope.launch {
            try {
                updateModelStatus(modelId, ModelStatus.DOWNLOADING)
                simulateDownload(modelId, resumeFromProgress = true)
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = "Failed to resume download: ${e.message}")
                }
            }
        }
    }

    fun cancelDownload(modelId: String) {
        viewModelScope.launch {
            try {
                updateModelStatus(modelId, ModelStatus.AVAILABLE, resetProgress = true)
                _uiState.update { 
                    it.copy(error = "Download cancelled")
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = "Failed to cancel download: ${e.message}")
                }
            }
        }
    }

    fun deleteModel(modelId: String) {
        val model = _uiState.value.models.find { it.id == modelId }
        if (model != null) {
            _uiState.update { it.copy(modelToDelete = model) }
        }
    }

    fun confirmDeleteModel() {
        val modelToDelete = _uiState.value.modelToDelete
        if (modelToDelete != null) {
            viewModelScope.launch {
                try {
                    modelRepository.deleteModel(modelToDelete.id)
                    _uiState.update { 
                        it.copy(
                            modelToDelete = null,
                            error = "Model deleted successfully"
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update { 
                        it.copy(
                            modelToDelete = null,
                            error = "Failed to delete model: ${e.message}"
                        )
                    }
                }
            }
        }
    }

    fun cancelDeleteModel() {
        _uiState.update { it.copy(modelToDelete = null) }
    }

    fun setPrimaryModel(modelId: String) {
        viewModelScope.launch {
            try {
                // First, unset all other primary models
                val models = _uiState.value.models.map { model ->
                    model.copy(isPrimary = model.id == modelId)
                }
                
                // Update repository
                modelRepository.setPrimaryModel(modelId)
                
                _uiState.update { state ->
                    state.copy(
                        models = models,
                        filteredModels = filterModels(models, state.selectedFilter),
                        error = "Primary model updated"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = "Failed to set primary model: ${e.message}")
                }
            }
        }
    }

    fun showAddModelDialog() {
        _uiState.update { it.copy(showAddModelDialog = true) }
    }

    fun hideAddModelDialog() {
        _uiState.update { it.copy(showAddModelDialog = false) }
    }

    fun addCustomModel(url: String, name: String) {
        viewModelScope.launch {
            try {
                val newModel = Model(
                    id = "custom_${System.currentTimeMillis()}",
                    name = name,
                    displayName = name,
                    description = "Custom model from URL",
                    author = "Custom",
                    size = 2L * 1024 * 1024 * 1024, // 2GB estimate
                    downloadUrl = url,
                    modelPath = null,
                    isDownloaded = false,
                    isDownloading = false,
                    downloadProgress = 0f,
                    status = ModelStatus.AVAILABLE,
                    isPrimary = false,
                    supportedTasks = listOf(TaskType.CHAT),
                    parameters = ModelParameters(
                        contextLength = 4096,
                        vocabularySize = 32000,
                        layerCount = 32,
                        hiddenSize = 4096,
                        attentionHeads = 32,
                        intermediateSize = 11008,
                        architecture = "unknown",
                        quantization = "fp16",
                        license = "Apache-2.0"
                    )
                )
                
                modelRepository.insertModel(newModel)
                
                _uiState.update { 
                    it.copy(
                        showAddModelDialog = false,
                        error = "Custom model added successfully"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        showAddModelDialog = false,
                        error = "Failed to add custom model: ${e.message}"
                    )
                }
            }
        }
    }

    fun importLocalModel(uri: android.net.Uri) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                
                // Extract file information
                val fileName = getFileNameFromUri(uri) ?: "unknown_model"
                val fileSize = getFileSizeFromUri(uri) ?: (1L * 1024 * 1024 * 1024) // Default 1GB
                
                // Debug logging
                println("ModelImport: Attempting to import file: $fileName")
                println("ModelImport: URI: $uri")
                
                // Validate file format (but be permissive for user-selected files)
                if (!isValidModelFile(fileName)) {
                    // Show warning but allow import anyway since user explicitly selected the file
                    println("ModelImport: File '$fileName' doesn't match common extensions, but importing anyway...")
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Warning: '$fileName' has an uncommon extension. Import may not work properly. Continuing anyway..."
                        )
                    }
                    // Don't return - continue with import
                    kotlinx.coroutines.delay(2000) // Show warning for 2 seconds
                    _uiState.update { it.copy(error = null, isLoading = true) }
                }
                
                // Copy file to app's models directory
                val destinationPath = copyModelFile(uri, fileName)
                
                // Create model entry
                val newModel = Model(
                    id = "local_${System.currentTimeMillis()}",
                    name = fileName.substringBeforeLast('.'),
                    displayName = fileName.substringBeforeLast('.').replace('_', ' ').replaceFirstChar { it.uppercase() },
                    description = "Imported from local storage",
                    author = "Local Import",
                    size = fileSize,
                    downloadUrl = "",
                    modelPath = destinationPath,
                    isDownloaded = true,
                    isDownloading = false,
                    downloadProgress = 1.0f,
                    status = ModelStatus.DOWNLOADED,
                    isPrimary = false,
                    supportedTasks = listOf(TaskType.CHAT, TaskType.ASK_IMAGE, TaskType.ASK_AUDIO), // Assume multi-modal
                    parameters = ModelParameters(
                        contextLength = 4096,
                        vocabularySize = 32000,
                        layerCount = 24,
                        hiddenSize = 2048,
                        attentionHeads = 16,
                        intermediateSize = 5632,
                        architecture = "Unknown",
                        quantization = "Unknown",
                        license = "Unknown"
                    ),
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                
                // Add to repository
                modelRepository.insertModel(newModel)
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Model imported successfully: ${newModel.displayName}"
                    )
                }
                
                // Refresh models list
                loadModels()
                
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to import model: ${e.message}"
                    )
                }
            }
        }
    }
    
    private fun getFileNameFromUri(uri: android.net.Uri): String? {
        // Try multiple methods to extract filename
        var fileName: String? = null
        
        // Method 1: Try last path segment first
        fileName = uri.lastPathSegment
        if (!fileName.isNullOrBlank() && fileName.contains('.')) {
            println("ModelImport: Found filename from lastPathSegment: $fileName")
            return fileName
        }
        
        // Method 2: Try extracting from full path
        fileName = uri.path?.substringAfterLast('/')
        if (!fileName.isNullOrBlank() && fileName.contains('.')) {
            println("ModelImport: Found filename from path: $fileName")
            return fileName
        }
        
        // Method 3: Try extracting from toString
        val uriString = uri.toString()
        fileName = uriString.substringAfterLast('/')
        if (fileName.contains('.')) {
            println("ModelImport: Found filename from URI string: $fileName")
            return fileName
        }
        
        println("ModelImport: Could not extract filename from URI: $uri")
        return "unknown_model.bin"
    }
    
    private fun getFileSizeFromUri(uri: android.net.Uri): Long? {
        // For now, return null and use default size estimation
        // In a real implementation, you'd use ContentResolver:
        // context.contentResolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)
        return null
    }
    
    private fun isValidModelFile(fileName: String): Boolean {
        val validExtensions = listOf(
            ".gguf",      // GGUF format (common for llama.cpp)
            ".bin",       // Binary format
            ".safetensors", // SafeTensors format
            ".tflite",    // TensorFlow Lite
            ".task",      // MediaPipe Tasks
            ".onnx",      // ONNX format
            ".pb",        // TensorFlow SavedModel
            ".pt",        // PyTorch
            ".pth",       // PyTorch
            ".h5",        // Keras/HDF5
            ".trt",       // TensorRT
            ".dlc"        // Qualcomm DLC
        )
        
        val fileNameLower = fileName.lowercase()
        println("ModelImport: Validating file: $fileName (lowercase: $fileNameLower)")
        
        for (extension in validExtensions) {
            if (fileNameLower.endsWith(extension)) {
                println("ModelImport: File matches extension: $extension")
                return true
            }
        }
        
        println("ModelImport: File does not match any valid extensions")
        println("ModelImport: Valid extensions: ${validExtensions.joinToString(", ")}")
        return false
    }
    
    private suspend fun copyModelFile(uri: android.net.Uri, fileName: String): String {
        // For demonstration, return the URI string
        // In a real implementation, you would:
        // 1. Create a destination file in app's private storage
        // 2. Copy the content from URI to the destination
        // 3. Return the absolute path of the copied file
        
        val destinationPath = "models/$fileName"
        
        // This is where you'd implement actual file copying:
        // val inputStream = context.contentResolver.openInputStream(uri)
        // val outputFile = File(context.filesDir, destinationPath)
        // outputFile.parentFile?.mkdirs()
        // inputStream?.use { input ->
        //     outputFile.outputStream().use { output ->
        //         input.copyTo(output)
        //     }
        // }
        
        return destinationPath
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun filterModels(models: List<Model>, filter: ModelFilter): List<Model> {
        return when (filter) {
            ModelFilter.ALL -> models
            ModelFilter.DOWNLOADED -> models.filter { it.status == ModelStatus.DOWNLOADED }
            ModelFilter.DOWNLOADING -> models.filter { 
                it.status in listOf(ModelStatus.DOWNLOADING, ModelStatus.PAUSED) 
            }
            ModelFilter.AVAILABLE -> models.filter { it.status == ModelStatus.AVAILABLE }
        }
    }

    private suspend fun updateModelStatus(
        modelId: String, 
        status: ModelStatus, 
        resetProgress: Boolean = false
    ) {
        val models = _uiState.value.models.toMutableList()
        val modelIndex = models.indexOfFirst { it.id == modelId }
        
        if (modelIndex != -1) {
            val model = models[modelIndex]
            models[modelIndex] = model.copy(
                status = status,
                downloadProgress = if (resetProgress) 0f else model.downloadProgress
            )
            
            _uiState.update { state ->
                state.copy(
                    models = models,
                    filteredModels = filterModels(models, state.selectedFilter)
                )
            }
        }
    }

    private suspend fun simulateDownload(
        modelId: String, 
        resumeFromProgress: Boolean = false
    ) {
        try {
            val startProgress = if (resumeFromProgress) {
                _uiState.value.models.find { it.id == modelId }?.downloadProgress ?: 0f
            } else {
                0f
            }
            
            for (progress in (startProgress * 100).toInt()..100 step 5) {
                // Check if download was cancelled or paused
                val currentModel = _uiState.value.models.find { it.id == modelId }
                if (currentModel?.status != ModelStatus.DOWNLOADING) {
                    break
                }
                
                val progressFloat = progress / 100f
                
                val models = _uiState.value.models.toMutableList()
                val modelIndex = models.indexOfFirst { it.id == modelId }
                
                if (modelIndex != -1) {
                    val model = models[modelIndex]
                    models[modelIndex] = model.copy(downloadProgress = progressFloat)
                    
                    _uiState.update { state ->
                        state.copy(
                            models = models,
                            filteredModels = filterModels(models, state.selectedFilter)
                        )
                    }
                }
                
                kotlinx.coroutines.delay(200) // Simulate download time
            }
            
            // Complete download
            val currentModel = _uiState.value.models.find { it.id == modelId }
            if (currentModel?.status == ModelStatus.DOWNLOADING) {
                updateModelStatus(modelId, ModelStatus.DOWNLOADED)
                
                // Update the repository
                modelRepository.updateModelStatus(modelId, ModelStatus.DOWNLOADED)
                
                _uiState.update { 
                    it.copy(error = "Model downloaded successfully")
                }
            }
            
        } catch (e: Exception) {
            updateModelStatus(modelId, ModelStatus.ERROR)
            _uiState.update { 
                it.copy(error = "Download failed: ${e.message}")
            }
        }
    }
}