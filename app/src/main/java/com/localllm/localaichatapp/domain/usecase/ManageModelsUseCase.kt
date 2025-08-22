package com.localllm.localaichatapp.domain.usecase

import com.localllm.localaichatapp.domain.model.AiModel
import com.localllm.localaichatapp.domain.model.ModelConfiguration
import com.localllm.localaichatapp.domain.repository.ModelRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ManageModelsUseCase @Inject constructor(
    private val modelRepository: ModelRepository
) {
    suspend fun getAvailableModels(): Flow<List<AiModel>> {
        return modelRepository.getAvailableModels()
    }
    
    suspend fun downloadModel(modelId: String): Flow<Float> {
        return modelRepository.downloadModel(modelId)
    }
    
    suspend fun initializeModel(
        modelId: String, 
        config: ModelConfiguration = ModelConfiguration()
    ): Result<Unit> {
        return modelRepository.initializeModel(modelId, config)
    }
    
    suspend fun deleteModel(modelId: String): Result<Unit> {
        return modelRepository.deleteModel(modelId)
    }
    
    suspend fun isModelReady(modelId: String): Boolean {
        return modelRepository.isModelReady(modelId)
    }
    
    suspend fun updateModelConfig(
        modelId: String, 
        config: ModelConfiguration
    ): Result<Unit> {
        return modelRepository.updateModelConfiguration(modelId, config)
    }
}