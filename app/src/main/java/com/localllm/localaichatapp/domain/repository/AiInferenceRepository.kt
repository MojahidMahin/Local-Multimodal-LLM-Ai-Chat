package com.localllm.localaichatapp.domain.repository

import android.net.Uri
import com.localllm.localaichatapp.domain.model.ChatMessage
import com.localllm.localaichatapp.domain.model.Model
import com.localllm.localaichatapp.domain.model.PromptTemplate
import com.localllm.localaichatapp.domain.model.StreamingResponse
import com.localllm.localaichatapp.domain.model.TaskType
import kotlinx.coroutines.flow.Flow

interface AiInferenceRepository {
    suspend fun generateResponse(
        modelId: String,
        input: String,
        taskType: TaskType = TaskType.CHAT,
        conversationHistory: List<String> = emptyList(),
        systemPrompt: String? = null
    ): Flow<StreamingResponse>
    
    // New methods for ViewModels
    suspend fun generateChatResponse(
        model: Model,
        messages: List<ChatMessage>,
        userInput: String
    ): Flow<StreamingResponse>
    
    suspend fun generateImageAnalysis(
        model: Model,
        messages: List<ChatMessage>,
        userInput: String,
        imageUri: Uri?
    ): Flow<StreamingResponse>
    
    suspend fun generateAudioAnalysis(
        model: Model,
        messages: List<ChatMessage>,
        userInput: String,
        audioUri: Uri?
    ): Flow<StreamingResponse>
    
    suspend fun generatePromptLabResponse(
        model: Model,
        prompt: String,
        template: PromptTemplate?
    ): Flow<StreamingResponse>
    
    suspend fun generateResponseWithImage(
        modelId: String,
        input: String,
        imageUri: String,
        taskType: TaskType = TaskType.ASK_IMAGE,
        conversationHistory: List<String> = emptyList()
    ): Flow<StreamingResponse>
    
    suspend fun generateResponseWithAudio(
        modelId: String,
        input: String,
        audioUri: String,
        taskType: TaskType = TaskType.ASK_AUDIO,
        conversationHistory: List<String> = emptyList()
    ): Flow<StreamingResponse>
    
    suspend fun analyzeImage(
        modelId: String,
        imageUri: String,
        query: String = "Describe this image in detail."
    ): Flow<StreamingResponse>
    
    suspend fun processAudio(
        modelId: String,
        audioUri: String,
        task: String = "transcribe" // transcribe, summarize, analyze
    ): Flow<StreamingResponse>
    
    suspend fun generateWithTemplate(
        modelId: String,
        template: String,
        parameters: Map<String, String> = emptyMap()
    ): Flow<StreamingResponse>
    
    suspend fun cancelGeneration(modelId: String): Result<Unit>
    suspend fun resetSession(modelId: String): Result<Unit>
    suspend fun isModelLoaded(modelId: String): Boolean
    suspend fun loadModel(modelId: String): Result<Unit>
    suspend fun unloadModel(modelId: String): Result<Unit>
    suspend fun getModelInfo(modelId: String): Result<Map<String, Any>>
}