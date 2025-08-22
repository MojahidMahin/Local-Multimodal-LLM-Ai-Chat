package com.localllm.localaichatapp.data.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.localllm.localaichatapp.data.local.database.entity.ModelEntity
import com.localllm.localaichatapp.domain.model.Model
import com.localllm.localaichatapp.domain.model.ModelParameters
import com.localllm.localaichatapp.domain.model.TaskType

class ModelMapper {
    private val gson = Gson()
    
    fun toEntity(domain: Model): ModelEntity {
        return ModelEntity(
            id = domain.id,
            name = domain.name,
            displayName = domain.displayName,
            description = domain.description,
            author = domain.author,
            size = domain.size,
            downloadUrl = domain.downloadUrl,
            modelPath = domain.modelPath,
            isDownloaded = domain.isDownloaded,
            isDownloading = domain.isDownloading,
            downloadProgress = domain.downloadProgress,
            supportedTasks = domain.supportedTasks.map { it.name },
            parameters = domain.parameters?.let { gson.toJson(it) },
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }
    
    fun toDomain(entity: ModelEntity): Model {
        val supportedTasks = try {
            entity.supportedTasks.mapNotNull { taskName ->
                TaskType.values().find { it.name == taskName }
            }
        } catch (e: Exception) {
            emptyList()
        }
        
        val parameters = try {
            entity.parameters?.let { 
                gson.fromJson(it, ModelParameters::class.java)
            }
        } catch (e: Exception) {
            null
        }
        
        return Model(
            id = entity.id,
            name = entity.name,
            displayName = entity.displayName,
            description = entity.description,
            author = entity.author,
            size = entity.size,
            downloadUrl = entity.downloadUrl,
            modelPath = entity.modelPath,
            isDownloaded = entity.isDownloaded,
            isDownloading = entity.isDownloading,
            downloadProgress = entity.downloadProgress,
            supportedTasks = supportedTasks,
            parameters = parameters,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
    
    fun toDomainList(entities: List<ModelEntity>): List<Model> {
        return entities.map { toDomain(it) }
    }
    
    fun toEntityList(domains: List<Model>): List<ModelEntity> {
        return domains.map { toEntity(it) }
    }
}