package com.localllm.localaichatapp.domain.usecase

import com.localllm.localaichatapp.domain.model.Model
import com.localllm.localaichatapp.domain.repository.ModelRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ManageModelsUseCase @Inject constructor(
    private val modelRepository: ModelRepository
) {
    fun getAllModels(): Flow<List<Model>> {
        return modelRepository.getAllModels()
    }
    
    fun getDownloadedModels(): Flow<List<Model>> {
        return modelRepository.getDownloadedModels()
    }
    
    suspend fun downloadModel(modelId: String, onProgress: (Float) -> Unit): Result<String> {
        return modelRepository.downloadModel(modelId, onProgress)
    }
    
    suspend fun initializeModel(modelId: String): Result<Unit> {
        return modelRepository.initializeModel(modelId)
    }
    
    suspend fun deleteModel(modelId: String) {
        modelRepository.deleteModel(modelId)
    }
    
    suspend fun isModelReady(modelId: String): Boolean {
        return modelRepository.isModelReady(modelId)
    }
    
    suspend fun cancelDownload(modelId: String): Result<Unit> {
        return modelRepository.cancelDownload(modelId)
    }
}