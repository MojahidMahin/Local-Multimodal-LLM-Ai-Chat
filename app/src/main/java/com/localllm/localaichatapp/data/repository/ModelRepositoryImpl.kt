package com.localllm.localaichatapp.data.repository

import android.content.Context
import com.localllm.localaichatapp.data.local.database.dao.ModelDao
import com.localllm.localaichatapp.data.mapper.ModelMapper
import com.localllm.localaichatapp.data.remote.ModelApiService
import com.localllm.localaichatapp.data.remote.ModelDownloadManager
import com.localllm.localaichatapp.domain.model.Model
import com.localllm.localaichatapp.domain.model.TaskType
import com.localllm.localaichatapp.domain.repository.ModelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModelRepositoryImpl @Inject constructor(
    private val modelDao: ModelDao,
    private val modelMapper: ModelMapper,
    private val modelApiService: ModelApiService,
    private val downloadManager: ModelDownloadManager,
    private val context: Context
) : ModelRepository {
    
    override fun getAllModels(): Flow<List<Model>> {
        return modelDao.getAllModels().map { entities ->
            modelMapper.toDomainList(entities)
        }
    }
    
    override fun getDownloadedModels(): Flow<List<Model>> {
        return modelDao.getDownloadedModels().map { entities ->
            modelMapper.toDomainList(entities)
        }
    }
    
    override fun getDownloadingModels(): Flow<List<Model>> {
        return modelDao.getDownloadingModels().map { entities ->
            modelMapper.toDomainList(entities)
        }
    }
    
    override fun getModelsForTask(taskType: TaskType): Flow<List<Model>> {
        return modelDao.getModelsForTask(taskType.name).map { entities ->
            modelMapper.toDomainList(entities)
        }
    }
    
    override fun getModelById(modelId: String): Flow<Model?> {
        return modelDao.getModelByIdFlow(modelId).map { entity ->
            entity?.let { modelMapper.toDomain(it) }
        }
    }
    
    override suspend fun getModelByIdSuspend(modelId: String): Model? {
        return modelDao.getModelById(modelId)?.let { entity ->
            modelMapper.toDomain(entity)
        }
    }
    
    override suspend fun insertModel(model: Model) {
        modelDao.insertModel(modelMapper.toEntity(model))
    }
    
    override suspend fun insertModels(models: List<Model>) {
        modelDao.insertModels(modelMapper.toEntityList(models))
    }
    
    override suspend fun updateModel(model: Model) {
        modelDao.updateModel(modelMapper.toEntity(model))
    }
    
    override suspend fun updateDownloadStatus(
        modelId: String,
        isDownloaded: Boolean,
        isDownloading: Boolean,
        progress: Float,
        modelPath: String?
    ) {
        modelDao.updateDownloadStatus(
            modelId = modelId,
            isDownloaded = isDownloaded,
            isDownloading = isDownloading,
            progress = progress,
            modelPath = modelPath
        )
    }
    
    override suspend fun deleteModel(modelId: String) {
        // Delete local model file if exists
        val model = modelDao.getModelById(modelId)
        model?.modelPath?.let { path ->
            downloadManager.deleteModelFile(path)
        }
        modelDao.deleteModelById(modelId)
    }
    
    override suspend fun getModelCount(): Int {
        return modelDao.getModelCount()
    }
    
    override suspend fun getTotalDownloadedSize(): Long {
        return modelDao.getTotalDownloadedSize() ?: 0L
    }
    
    override suspend fun refreshAvailableModels(): Result<List<Model>> {
        return try {
            val remoteModels = modelApiService.getAvailableModels()
            val models = remoteModels.map { apiModel ->
                Model(
                    id = apiModel.id,
                    name = apiModel.name,
                    displayName = apiModel.displayName ?: apiModel.name,
                    description = apiModel.description ?: "",
                    author = apiModel.author ?: "Unknown",
                    size = apiModel.size,
                    downloadUrl = apiModel.downloadUrl,
                    supportedTasks = apiModel.supportedTasks?.mapNotNull { taskName ->
                        TaskType.values().find { it.name.equals(taskName, ignoreCase = true) }
                    } ?: listOf(TaskType.CHAT),
                    parameters = apiModel.parameters
                )
            }
            insertModels(models)
            Result.success(models)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun downloadModel(modelId: String, onProgress: (Float) -> Unit): Result<String> {
        return try {
            val model = modelDao.getModelById(modelId)
                ?: return Result.failure(Exception("Model not found"))
            
            updateDownloadStatus(modelId, false, true, 0f, null)
            
            val modelPath = downloadManager.downloadModel(
                downloadUrl = model.downloadUrl,
                modelId = modelId,
                onProgress = { progress ->
                    updateDownloadStatus(modelId, false, true, progress, null)
                    onProgress(progress)
                }
            )
            
            updateDownloadStatus(modelId, true, false, 1f, modelPath)
            Result.success(modelPath)
        } catch (e: Exception) {
            updateDownloadStatus(modelId, false, false, 0f, null)
            Result.failure(e)
        }
    }
    
    override suspend fun cancelDownload(modelId: String): Result<Unit> {
        return try {
            downloadManager.cancelDownload(modelId)
            updateDownloadStatus(modelId, false, false, 0f, null)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun initializeModel(modelId: String): Result<Unit> {
        return try {
            val model = modelDao.getModelById(modelId)
            if (model?.isDownloaded == true && model.modelPath != null) {
                // Initialize the model with AI Edge runtime
                downloadManager.initializeModel(modelId, model.modelPath)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Model not downloaded"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun isModelReady(modelId: String): Boolean {
        return try {
            val model = modelDao.getModelById(modelId)
            model?.isDownloaded == true && 
            model.modelPath != null &&
            downloadManager.isModelInitialized(modelId)
        } catch (e: Exception) {
            false
        }
    }
}