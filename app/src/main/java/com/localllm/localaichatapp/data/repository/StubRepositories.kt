package com.localllm.localaichatapp.data.repository

import com.localllm.localaichatapp.domain.model.*
import com.localllm.localaichatapp.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StubChatRepository @Inject constructor() : ChatRepository {
    override suspend fun getSession(sessionId: String): ChatSession? {
        return ChatSession(
            id = sessionId,
            title = "Test Session",
            modelId = "test-model",
            messages = emptyList(),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }
    
    override suspend fun createSession(modelId: String, title: String): ChatSession {
        return ChatSession(
            id = "session-1",
            title = title,
            modelId = modelId,
            messages = emptyList(),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }
    
    override suspend fun getAllSessions(): Flow<List<ChatSession>> = flowOf(emptyList())
    
    override fun observeSession(sessionId: String): Flow<ChatSession?> {
        return flowOf(
            ChatSession(
                id = sessionId,
                title = "Test Chat",
                modelId = "test-model",
                messages = emptyList(),
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        )
    }
    
    override suspend fun deleteSession(sessionId: String): Result<Unit> = Result.success(Unit)
    override suspend fun clearMessages(sessionId: String): Result<Unit> = Result.success(Unit)
    override suspend fun addMessage(sessionId: String, message: ChatMessage): Result<Unit> = Result.success(Unit)
    override suspend fun updateMessage(sessionId: String, messageId: String, updatedMessage: ChatMessage): Result<Unit> = Result.success(Unit)
    override fun observeMessages(sessionId: String): Flow<List<ChatMessage>> = flowOf(emptyList())
}

@Singleton
class StubAiInferenceRepository @Inject constructor() : AiInferenceRepository {
    override suspend fun generateResponse(
        modelId: String,
        message: String,
        conversationHistory: List<String>
    ): Flow<StreamingResponse> = flowOf(
        StreamingResponse("Hello! This is a test response.", isComplete = true)
    )
    
    override suspend fun generateResponseWithImage(
        modelId: String,
        message: String,
        imageUri: String,
        conversationHistory: List<String>
    ): Flow<StreamingResponse> = flowOf(
        StreamingResponse("I can see the image. This is a test response.", isComplete = true)
    )
    
    override suspend fun cancelGeneration(modelId: String): Result<Unit> = Result.success(Unit)
    override suspend fun resetSession(modelId: String): Result<Unit> = Result.success(Unit)
}

@Singleton
class StubModelRepository @Inject constructor() : ModelRepository {
    override suspend fun isModelReady(modelId: String): Boolean = true
    
    override suspend fun getAvailableModels(): Flow<List<AiModel>> = flowOf(
        listOf(
            AiModel(
                id = "test-model",
                name = "Test Model",
                description = "A test model for demonstration",
                isDownloaded = true,
                isInitialized = true,
                modelSize = 1_000_000_000L,
                supportedFeatures = setOf(ModelFeature.TEXT_GENERATION)
            )
        )
    )
    
    override suspend fun downloadModel(modelId: String): Flow<Float> = flowOf(1.0f)
    
    override suspend fun initializeModel(modelId: String, config: ModelConfiguration): Result<Unit> = 
        Result.success(Unit)
        
    override suspend fun deleteModel(modelId: String): Result<Unit> = Result.success(Unit)
    
    override suspend fun updateModelConfiguration(modelId: String, config: ModelConfiguration): Result<Unit> = 
        Result.success(Unit)
        
    override suspend fun getModel(modelId: String): AiModel? = AiModel(
        id = modelId,
        name = "Test Model",
        description = "A test model",
        isDownloaded = true,
        isInitialized = true
    )
    
    override suspend fun getModelConfiguration(modelId: String): ModelConfiguration? = 
        ModelConfiguration()
}