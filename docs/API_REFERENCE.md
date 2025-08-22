# API Reference Documentation

## 🔌 Repository Interfaces

### ModelRepository

#### Core Operations
```kotlin
// Get models
fun getAllModels(): Flow<List<Model>>
fun getDownloadedModels(): Flow<List<Model>>
fun getModelsForTask(taskType: TaskType): Flow<List<Model>>
fun getModelById(modelId: String): Flow<Model?>

// Model management
suspend fun downloadModel(modelId: String, onProgress: (Float) -> Unit): Result<String>
suspend fun cancelDownload(modelId: String): Result<Unit>
suspend fun initializeModel(modelId: String): Result<Unit>
suspend fun isModelReady(modelId: String): Boolean
```

### ChatRepository

#### Session Management
```kotlin
// Create and manage sessions
suspend fun createSession(modelId: String, title: String, taskType: TaskType): ChatSession
fun getAllSessions(): Flow<List<ChatSession>>
fun getSessionsByTaskType(taskType: TaskType): Flow<List<ChatSession>>
fun getBookmarkedSessions(): Flow<List<ChatSession>>
fun searchSessions(query: String): Flow<List<ChatSession>>

// Session operations
suspend fun updateBookmarkStatus(sessionId: String, isBookmarked: Boolean): Result<Unit>
suspend fun deleteSession(sessionId: String): Result<Unit>
```

#### Message Management
```kotlin
// Message operations
suspend fun addMessage(sessionId: String, message: ChatMessage): Result<Unit>
fun observeMessages(sessionId: String): Flow<List<ChatMessage>>
fun getAllImageMessages(): Flow<List<ChatMessage>>
fun getAllAudioMessages(): Flow<List<ChatMessage>>

// Message queries
suspend fun getMessageCount(sessionId: String): Int
suspend fun deleteOldMessages(cutoffTime: Long)
```

### AiInferenceRepository

#### Core Generation
```kotlin
// Text generation
suspend fun generateResponse(
    modelId: String,
    input: String,
    taskType: TaskType = TaskType.CHAT,
    conversationHistory: List<String> = emptyList(),
    systemPrompt: String? = null
): Flow<StreamingResponse>

// Multimodal generation
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
```

#### Specialized Operations
```kotlin
// Image analysis
suspend fun analyzeImage(
    modelId: String,
    imageUri: String,
    query: String = "Describe this image in detail."
): Flow<StreamingResponse>

// Audio processing
suspend fun processAudio(
    modelId: String,
    audioUri: String,
    task: String = "transcribe"
): Flow<StreamingResponse>

// Template-based generation
suspend fun generateWithTemplate(
    modelId: String,
    template: String,
    parameters: Map<String, String> = emptyMap()
): Flow<StreamingResponse>
```

#### Model Control
```kotlin
// Model lifecycle
suspend fun isModelLoaded(modelId: String): Boolean
suspend fun loadModel(modelId: String): Result<Unit>
suspend fun unloadModel(modelId: String): Result<Unit>
suspend fun getModelInfo(modelId: String): Result<Map<String, Any>>

// Generation control
suspend fun cancelGeneration(modelId: String): Result<Unit>
suspend fun resetSession(modelId: String): Result<Unit>
```

### PromptTemplateRepository

#### Template Management
```kotlin
// Get templates
fun getAllTemplates(): Flow<List<PromptTemplate>>
fun getTemplatesByCategory(category: PromptCategory): Flow<List<PromptTemplate>>
fun getBuiltInTemplates(): Flow<List<PromptTemplate>>
fun getCustomTemplates(): Flow<List<PromptTemplate>>
fun getMostUsedTemplates(limit: Int = 10): Flow<List<PromptTemplate>>

// Template operations
suspend fun getTemplateById(templateId: String): PromptTemplate?
suspend fun insertTemplate(template: PromptTemplate)
suspend fun updateTemplate(template: PromptTemplate)
suspend fun deleteTemplate(templateId: String)
suspend fun incrementUsageCount(templateId: String)
```

### BenchmarkRepository

#### Metrics Collection
```kotlin
// Benchmark data
fun getAllBenchmarks(): Flow<List<Benchmark>>
fun getBenchmarksByModel(modelId: String): Flow<List<Benchmark>>
fun getBenchmarksBySession(sessionId: String): Flow<List<Benchmark>>
fun getBenchmarksByTaskType(taskType: TaskType): Flow<List<Benchmark>>

// Analytics
suspend fun getAverageTTFT(modelId: String, taskType: TaskType): Float?
suspend fun getAverageDecodeSpeed(modelId: String, taskType: TaskType): Float?
suspend fun getAverageLatency(modelId: String, taskType: TaskType): Float?
suspend fun getBenchmarkSummary(modelId: String, taskType: TaskType): BenchmarkSummary?
```

## 📊 Data Models

### Core Models

#### TaskType
```kotlin
enum class TaskType(val displayName: String, val description: String) {
    CHAT("AI Chat", "Multi-turn conversational AI"),
    ASK_IMAGE("Ask Image", "Upload images and ask questions about them"),
    ASK_AUDIO("Ask Audio", "Process and analyze audio inputs"),
    PROMPT_LAB("Prompt Lab", "Single-turn AI interactions with templates")
}
```

#### Model
```kotlin
data class Model(
    val id: String,
    val name: String,
    val displayName: String,
    val description: String,
    val author: String,
    val size: Long,
    val downloadUrl: String,
    val modelPath: String? = null,
    val isDownloaded: Boolean = false,
    val isDownloading: Boolean = false,
    val downloadProgress: Float = 0f,
    val supportedTasks: List<TaskType> = emptyList(),
    val parameters: ModelParameters? = null
) {
    val formattedSize: String
    val isAvailable: Boolean
}
```

#### ChatMessage Hierarchy
```kotlin
sealed class ChatMessage {
    abstract val id: String
    abstract val timestamp: Long
    abstract val sender: ChatSender
    abstract val tokenCount: Int?
    abstract val responseTimeMs: Long?
}

data class TextMessage(...) : ChatMessage()
data class ImageMessage(...) : ChatMessage()
data class AudioMessage(...) : ChatMessage()
data class BenchmarkMessage(...) : ChatMessage()
data class LoadingMessage(...) : ChatMessage()
data class ErrorMessage(...) : ChatMessage()
```

#### StreamingResponse
```kotlin
data class StreamingResponse(
    val content: String,
    val isComplete: Boolean,
    val metadata: ResponseMetadata? = null
)

data class ResponseMetadata(
    val timeToFirstToken: Long? = null,
    val tokensPerSecond: Float? = null,
    val totalInputTokens: Int? = null,
    val totalOutputTokens: Int? = null,
    val latencyMs: Long? = null,
    val memoryUsageMb: Float? = null,
    val cpuUsagePercent: Float? = null,
    val batteryLevel: Float? = null
)
```

## 🔄 Usage Examples

### Basic Chat
```kotlin
// Create a chat session
val session = chatRepository.createSession(
    modelId = "gemma-2b",
    title = "General Chat",
    taskType = TaskType.CHAT
)

// Send a message
val userMessage = TextMessage(
    sender = ChatSender.USER,
    content = "Hello, how are you?"
)
chatRepository.addMessage(session.id, userMessage)

// Generate AI response
aiInferenceRepository.generateResponse(
    modelId = "gemma-2b",
    input = "Hello, how are you?",
    taskType = TaskType.CHAT
).collect { response ->
    if (!response.isComplete) {
        // Handle streaming content
        updateUI(response.content)
    } else {
        // Handle completion with metadata
        handleBenchmark(response.metadata)
    }
}
```

### Image Analysis
```kotlin
// Create Ask Image session
val session = chatRepository.createSession(
    modelId = "gemma-7b",
    title = "Image Analysis",
    taskType = TaskType.ASK_IMAGE
)

// Analyze image
aiInferenceRepository.analyzeImage(
    modelId = "gemma-7b",
    imageUri = imageUri,
    query = "What do you see in this image?"
).collect { response ->
    // Handle streaming analysis
    handleImageAnalysis(response)
}
```

### Template-based Generation
```kotlin
// Get a template
val template = promptTemplateRepository.getTemplateById("summarize-article")

// Use template with parameters
val parameters = mapOf(
    "text" to "Long article content here..."
)

aiInferenceRepository.generateWithTemplate(
    modelId = "phi-3-mini",
    template = template.template,
    parameters = parameters
).collect { response ->
    handleTemplateResponse(response)
}
```

## 🛠️ Error Handling

### Result Wrapper
```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Failure(val exception: Throwable) : Result<Nothing>()
}

// Usage
when (val result = modelRepository.downloadModel(modelId) { progress ->
    updateProgress(progress)
}) {
    is Result.Success -> handleSuccess(result.data)
    is Result.Failure -> handleError(result.exception)
}
```

### Common Exceptions
- `IllegalStateException` - Model not ready/downloaded
- `IllegalArgumentException` - Invalid parameters
- `IOException` - Network/file system errors
- `SecurityException` - Permission issues