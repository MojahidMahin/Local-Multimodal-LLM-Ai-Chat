package com.localllm.localaichatapp.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "models")
data class ModelEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val displayName: String,
    val description: String,
    val author: String,
    val size: Long,
    val downloadUrl: String,
    val modelPath: String?,
    val isDownloaded: Boolean = false,
    val isDownloading: Boolean = false,
    val downloadProgress: Float = 0f,
    val supportedTasks: List<String>, // Chat, Image, Audio, PromptLab
    val parameters: String? = null, // JSON string of model parameters
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)