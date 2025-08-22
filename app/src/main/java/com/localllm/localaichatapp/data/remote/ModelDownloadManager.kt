package com.localllm.localaichatapp.data.remote

import android.content.Context
import kotlinx.coroutines.delay
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModelDownloadManager @Inject constructor(
    private val context: Context
) {
    private val activeDownloads = ConcurrentHashMap<String, Boolean>()
    private val initializedModels = ConcurrentHashMap<String, Boolean>()
    
    suspend fun downloadModel(
        downloadUrl: String,
        modelId: String,
        onProgress: (Float) -> Unit
    ): String {
        if (activeDownloads[modelId] == true) {
            throw IllegalStateException("Model is already being downloaded")
        }
        
        activeDownloads[modelId] = true
        
        try {
            // Create models directory if it doesn't exist
            val modelsDir = File(context.filesDir, "models")
            if (!modelsDir.exists()) {
                modelsDir.mkdirs()
            }
            
            val modelFile = File(modelsDir, "$modelId.task")
            
            // Simulate download progress
            for (i in 0..100 step 5) {
                if (activeDownloads[modelId] != true) {
                    throw InterruptedException("Download cancelled")
                }
                
                onProgress(i / 100f)
                delay(100) // Simulate download time
            }
            
            // Create a dummy model file for demonstration
            modelFile.writeText("Mock model data for $modelId")
            
            onProgress(1f)
            return modelFile.absolutePath
            
        } finally {
            activeDownloads.remove(modelId)
        }
    }
    
    suspend fun cancelDownload(modelId: String) {
        activeDownloads[modelId] = false
    }
    
    fun deleteModelFile(modelPath: String): Boolean {
        return try {
            val file = File(modelPath)
            if (file.exists()) {
                file.delete()
            } else {
                true
            }
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun initializeModel(modelId: String, modelPath: String) {
        // Simulate model initialization
        delay(500)
        initializedModels[modelId] = true
    }
    
    fun isModelInitialized(modelId: String): Boolean {
        return initializedModels[modelId] == true
    }
    
    fun getModelPath(modelId: String): String? {
        val modelsDir = File(context.filesDir, "models")
        val modelFile = File(modelsDir, "$modelId.task")
        return if (modelFile.exists()) {
            modelFile.absolutePath
        } else {
            null
        }
    }
}