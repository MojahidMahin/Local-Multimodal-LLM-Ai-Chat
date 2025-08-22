package com.localllm.localaichatapp.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.localllm.localaichatapp.data.local.database.entity.ModelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ModelDao {
    
    @Query("SELECT * FROM models ORDER BY name ASC")
    fun getAllModels(): Flow<List<ModelEntity>>
    
    @Query("SELECT * FROM models WHERE isDownloaded = 1 ORDER BY name ASC")
    fun getDownloadedModels(): Flow<List<ModelEntity>>
    
    @Query("SELECT * FROM models WHERE isDownloading = 1")
    fun getDownloadingModels(): Flow<List<ModelEntity>>
    
    @Query("SELECT * FROM models WHERE supportedTasks LIKE '%' || :taskType || '%' AND isDownloaded = 1")
    fun getModelsForTask(taskType: String): Flow<List<ModelEntity>>
    
    @Query("SELECT * FROM models WHERE id = :modelId")
    suspend fun getModelById(modelId: String): ModelEntity?
    
    @Query("SELECT * FROM models WHERE id = :modelId")
    fun getModelByIdFlow(modelId: String): Flow<ModelEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModel(model: ModelEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModels(models: List<ModelEntity>)
    
    @Update
    suspend fun updateModel(model: ModelEntity)
    
    @Query("UPDATE models SET isDownloaded = :isDownloaded, isDownloading = :isDownloading, downloadProgress = :progress, modelPath = :modelPath, updatedAt = :updatedAt WHERE id = :modelId")
    suspend fun updateDownloadStatus(
        modelId: String,
        isDownloaded: Boolean,
        isDownloading: Boolean,
        progress: Float,
        modelPath: String?,
        updatedAt: Long = System.currentTimeMillis()
    )
    
    @Delete
    suspend fun deleteModel(model: ModelEntity)
    
    @Query("DELETE FROM models WHERE id = :modelId")
    suspend fun deleteModelById(modelId: String)
    
    @Query("SELECT COUNT(*) FROM models")
    suspend fun getModelCount(): Int
    
    @Query("SELECT SUM(size) FROM models WHERE isDownloaded = 1")
    suspend fun getTotalDownloadedSize(): Long?
}