package com.localllm.localaichatapp.data.repository

import android.net.Uri
import com.localllm.localaichatapp.domain.model.*
import com.localllm.localaichatapp.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StubChatRepository @Inject constructor() : ChatRepository {
    override suspend fun createSession(modelId: String, title: String, taskType: TaskType): ChatSession {
        return ChatSession(
            id = "session-1",
            title = title,
            modelId = modelId,
            taskType = taskType,
            messages = emptyList(),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }
    
    override suspend fun getSession(sessionId: String): ChatSession? {
        return ChatSession(
            id = sessionId,
            title = "Test Session",
            modelId = "test-model",
            taskType = TaskType.CHAT,
            messages = emptyList(),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }
    
    override fun getAllSessions(): Flow<List<ChatSession>> = flowOf(emptyList())
    override fun getSessionsByTaskType(taskType: TaskType): Flow<List<ChatSession>> = flowOf(emptyList())
    override fun getSessionsByModel(modelId: String): Flow<List<ChatSession>> = flowOf(emptyList())
    override fun getBookmarkedSessions(): Flow<List<ChatSession>> = flowOf(emptyList())
    override fun searchSessions(query: String): Flow<List<ChatSession>> = flowOf(emptyList())
    override suspend fun addMessage(sessionId: String, message: ChatMessage): Result<Unit> = Result.success(Unit)
    override suspend fun updateMessage(messageId: String, updatedMessage: ChatMessage): Result<Unit> = Result.success(Unit)
    override suspend fun updateSession(session: ChatSession): Result<Unit> = Result.success(Unit)
    override suspend fun updateBookmarkStatus(sessionId: String, isBookmarked: Boolean): Result<Unit> = Result.success(Unit)
    override suspend fun deleteSession(sessionId: String): Result<Unit> = Result.success(Unit)
    override suspend fun deleteMessage(messageId: String): Result<Unit> = Result.success(Unit)
    override suspend fun clearMessages(sessionId: String): Result<Unit> = Result.success(Unit)
    override fun observeSession(sessionId: String): Flow<ChatSession?> = flowOf(null)
    override fun observeMessages(sessionId: String): Flow<List<ChatMessage>> = flowOf(emptyList())
    override fun getMessagesByType(sessionId: String, messageType: String): Flow<List<ChatMessage>> = flowOf(emptyList())
    override fun getAllImageMessages(): Flow<List<ChatMessage>> = flowOf(emptyList())
    override fun getAllAudioMessages(): Flow<List<ChatMessage>> = flowOf(emptyList())
    override suspend fun getMessageCount(sessionId: String): Int = 0
    override suspend fun deleteOldMessages(cutoffTime: Long) {}
}

@Singleton
class StubAiInferenceRepository @Inject constructor() : AiInferenceRepository {
    override suspend fun generateResponse(
        modelId: String,
        input: String,
        taskType: TaskType,
        conversationHistory: List<String>,
        systemPrompt: String?
    ): Flow<StreamingResponse> = flowOf(StreamingResponse("Test response", true))
    
    override suspend fun generateChatResponse(
        model: Model,
        messages: List<ChatMessage>,
        userInput: String
    ): Flow<StreamingResponse> = flowOf(StreamingResponse("Test chat response", true))
    
    override suspend fun generateImageAnalysis(
        model: Model,
        messages: List<ChatMessage>,
        userInput: String,
        imageUri: Uri?
    ): Flow<StreamingResponse> = flowOf(StreamingResponse("Test image analysis", true))
    
    override suspend fun generateAudioAnalysis(
        model: Model,
        messages: List<ChatMessage>,
        userInput: String,
        audioUri: Uri?
    ): Flow<StreamingResponse> = flowOf(StreamingResponse("Test audio analysis", true))
    
    override suspend fun generatePromptLabResponse(
        model: Model,
        prompt: String,
        template: PromptTemplate?
    ): Flow<StreamingResponse> = flowOf(StreamingResponse("Test prompt lab response", true))
    
    override suspend fun generateResponseWithImage(
        modelId: String,
        input: String,
        imageUri: String,
        taskType: TaskType,
        conversationHistory: List<String>
    ): Flow<StreamingResponse> = flowOf(StreamingResponse("Test image response", true))
    
    override suspend fun generateResponseWithAudio(
        modelId: String,
        input: String,
        audioUri: String,
        taskType: TaskType,
        conversationHistory: List<String>
    ): Flow<StreamingResponse> = flowOf(StreamingResponse("Test audio response", true))
    
    override suspend fun analyzeImage(
        modelId: String,
        imageUri: String,
        query: String
    ): Flow<StreamingResponse> = flowOf(StreamingResponse("Test image analysis", true))
    
    override suspend fun processAudio(
        modelId: String,
        audioUri: String,
        task: String
    ): Flow<StreamingResponse> = flowOf(StreamingResponse("Test audio processing", true))
    
    override suspend fun generateWithTemplate(
        modelId: String,
        template: String,
        parameters: Map<String, String>
    ): Flow<StreamingResponse> = flowOf(StreamingResponse("Test template response", true))
    
    override suspend fun cancelGeneration(modelId: String): Result<Unit> = Result.success(Unit)
    override suspend fun resetSession(modelId: String): Result<Unit> = Result.success(Unit)
    override suspend fun isModelLoaded(modelId: String): Boolean = true
    override suspend fun loadModel(modelId: String): Result<Unit> = Result.success(Unit)
    override suspend fun unloadModel(modelId: String): Result<Unit> = Result.success(Unit)
    override suspend fun getModelInfo(modelId: String): Result<Map<String, Any>> = Result.success(emptyMap())
}

@Singleton
class StubModelRepository @Inject constructor() : ModelRepository {
    override fun getAllModels(): Flow<List<Model>> = flowOf(emptyList())
    override fun getDownloadedModels(): Flow<List<Model>> = flowOf(emptyList())
    override fun getDownloadingModels(): Flow<List<Model>> = flowOf(emptyList())
    override fun getModelsForTask(taskType: TaskType): Flow<List<Model>> = flowOf(emptyList())
    override fun getModelById(modelId: String): Flow<Model?> = flowOf(null)
    override suspend fun getModelByIdSuspend(modelId: String): Model? = null
    override suspend fun insertModel(model: Model) {}
    override suspend fun insertModels(models: List<Model>) {}
    override suspend fun updateModel(model: Model) {}
    override suspend fun updateDownloadStatus(
        modelId: String,
        isDownloaded: Boolean,
        isDownloading: Boolean,
        progress: Float,
        modelPath: String?
    ) {}
    override suspend fun deleteModel(modelId: String) {}
    override suspend fun getModelCount(): Int = 0
    override suspend fun getTotalDownloadedSize(): Long = 0L
    override suspend fun refreshAvailableModels(): Result<List<Model>> = Result.success(emptyList())
    override suspend fun downloadModel(modelId: String, onProgress: (Float) -> Unit): Result<String> = Result.success("")
    override suspend fun cancelDownload(modelId: String): Result<Unit> = Result.success(Unit)
    override suspend fun updateModelStatus(modelId: String, status: ModelStatus) {}
    override suspend fun setPrimaryModel(modelId: String) {}
    override suspend fun initializeModel(modelId: String): Result<Unit> = Result.success(Unit)
    override suspend fun isModelReady(modelId: String): Boolean = false
}

@Singleton
class StubBenchmarkRepository @Inject constructor() : BenchmarkRepository {
    override fun getAllBenchmarks(): Flow<List<Benchmark>> = flowOf(emptyList())
    override fun getBenchmarksByModel(modelId: String): Flow<List<Benchmark>> = flowOf(emptyList())
    override fun getBenchmarksBySession(sessionId: String): Flow<List<Benchmark>> = flowOf(emptyList())
    override fun getBenchmarksByTaskType(taskType: TaskType): Flow<List<Benchmark>> = flowOf(emptyList())
    override fun getRecentBenchmarks(modelId: String, taskType: TaskType, limit: Int): Flow<List<Benchmark>> = flowOf(emptyList())
    override suspend fun insertBenchmark(benchmark: Benchmark) {}
    override suspend fun getAverageTTFT(modelId: String, taskType: TaskType): Float? = null
    override suspend fun getAverageDecodeSpeed(modelId: String, taskType: TaskType): Float? = null
    override suspend fun getAverageLatency(modelId: String, taskType: TaskType): Float? = null
    override suspend fun getBenchmarkSummary(modelId: String, taskType: TaskType): BenchmarkSummary? = null
    override suspend fun deleteOldBenchmarks(cutoffTime: Long) {}
    override suspend fun deleteBenchmarksByModel(modelId: String) {}
    override suspend fun deleteAllBenchmarks() {}
}