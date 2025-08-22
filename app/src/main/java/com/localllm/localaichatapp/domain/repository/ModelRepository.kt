package com.localllm.localaichatapp.domain.repository

import com.localllm.localaichatapp.domain.model.Model
import com.localllm.localaichatapp.domain.model.ModelStatus
import com.localllm.localaichatapp.domain.model.TaskType
import kotlinx.coroutines.flow.Flow

interface ModelRepository {
    fun getAllModels(): Flow<List<Model>>
    fun getDownloadedModels(): Flow<List<Model>>
    fun getDownloadingModels(): Flow<List<Model>>
    fun getModelsForTask(taskType: TaskType): Flow<List<Model>>
    fun getModelById(modelId: String): Flow<Model?>
    suspend fun getModelByIdSuspend(modelId: String): Model?
    suspend fun insertModel(model: Model)
    suspend fun insertModels(models: List<Model>)
    suspend fun updateModel(model: Model)
    suspend fun updateDownloadStatus(
        modelId: String,
        isDownloaded: Boolean,
        isDownloading: Boolean,
        progress: Float,
        modelPath: String?
    )
    suspend fun deleteModel(modelId: String)
    suspend fun getModelCount(): Int
    suspend fun getTotalDownloadedSize(): Long
    suspend fun refreshAvailableModels(): Result<List<Model>>
    suspend fun downloadModel(modelId: String, onProgress: (Float) -> Unit): Result<String>
    suspend fun cancelDownload(modelId: String): Result<Unit>
    suspend fun updateModelStatus(modelId: String, status: ModelStatus)
    suspend fun setPrimaryModel(modelId: String)
    suspend fun initializeModel(modelId: String): Result<Unit>
    suspend fun isModelReady(modelId: String): Boolean
}