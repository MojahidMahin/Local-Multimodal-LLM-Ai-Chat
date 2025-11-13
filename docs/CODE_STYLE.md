# Code Style Guide

This document defines the coding standards and conventions for LocalAiChatApp.

## Table of Contents
- [General Principles](#general-principles)
- [Kotlin Style](#kotlin-style)
- [Project Structure](#project-structure)
- [Naming Conventions](#naming-conventions)
- [Code Organization](#code-organization)
- [Documentation](#documentation)
- [Android-Specific Guidelines](#android-specific-guidelines)
- [Git Practices](#git-practices)

## General Principles

### 1. Readability First

Code is read more often than it's written. Prioritize clarity over cleverness.

```kotlin
// Good: Clear and explicit
fun calculateTotalTokensForSession(sessionId: String): Int {
    val messages = repository.getMessages(sessionId)
    return messages.sumOf { it.tokenCount }
}

// Bad: Overly compact and unclear
fun calc(s: String) = repo.get(s).sumOf { it.tc }
```

### 2. Consistency

Follow existing patterns in the codebase. If you're unsure, look at similar code.

### 3. SOLID Principles

- **Single Responsibility**: One class, one job
- **Open/Closed**: Open for extension, closed for modification
- **Liskov Substitution**: Subtypes must be substitutable
- **Interface Segregation**: Many specific interfaces > one general
- **Dependency Inversion**: Depend on abstractions, not concretions

### 4. DRY (Don't Repeat Yourself)

Extract repeated code into reusable functions or classes.

```kotlin
// Good: Reusable function
private fun formatTimestamp(timestamp: Long): String {
    return SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        .format(Date(timestamp))
}

// Bad: Repeated code
val time1 = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    .format(Date(timestamp1))
val time2 = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    .format(Date(timestamp2))
```

### 5. KISS (Keep It Simple, Stupid)

Simple solutions are better than complex ones.

## Kotlin Style

### Follow Official Kotlin Coding Conventions

Base reference: [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)

### Indentation and Formatting

- **Indentation**: 4 spaces (no tabs)
- **Line length**: 120 characters maximum (100 preferred)
- **Blank lines**: Use to separate logical sections

```kotlin
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val aiRepository: AiInferenceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun sendMessage(message: String) {
        // Implementation
    }
}
```

### Properties

```kotlin
// Prefer val over var
val immutableValue = 42
var mutableValue = 42

// Property with backing field
private val _messages = MutableStateFlow<List<Message>>(emptyList())
val messages: StateFlow<List<Message>> = _messages.asStateFlow()

// Lazy initialization
val heavyObject: HeavyObject by lazy {
    HeavyObject()
}

// Delegated properties
var userName: String by Delegates.observable("") { _, old, new ->
    println("Username changed from $old to $new")
}
```

### Functions

```kotlin
// Single expression functions
fun add(a: Int, b: Int) = a + b

// Block body for complex logic
fun processMessage(message: String): Result<ProcessedMessage> {
    if (message.isBlank()) {
        return Result.Error(InvalidMessageException())
    }

    val processed = message.trim()
    return Result.Success(ProcessedMessage(processed))
}

// Named parameters for clarity
fun createSession(
    modelId: String,
    title: String = "New Chat",
    taskType: TaskType = TaskType.CHAT
): ChatSession

// Use default parameters instead of overloads
fun generateResponse(
    input: String,
    temperature: Float = 0.7f,
    maxTokens: Int = 2048
): Flow<Response>
```

### Classes and Objects

```kotlin
// Data classes for simple data holders
data class ChatMessage(
    val id: String,
    val content: String,
    val timestamp: Long,
    val isFromUser: Boolean
)

// Sealed classes for restricted hierarchies
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

// Companion objects for constants and factory methods
class ChatRepository {
    companion object {
        private const val TAG = "ChatRepository"
        const val MAX_MESSAGES_PER_SESSION = 1000

        fun create(dao: ChatDao): ChatRepository {
            return ChatRepositoryImpl(dao)
        }
    }
}
```

### Control Flow

```kotlin
// Prefer when over if-else chains
when (taskType) {
    TaskType.CHAT -> handleChat()
    TaskType.ASK_IMAGE -> handleImage()
    TaskType.ASK_AUDIO -> handleAudio()
    TaskType.PROMPT_LAB -> handlePromptLab()
}

// Use if as expression
val status = if (isSuccess) "Success" else "Failed"

// Elvis operator for null handling
val name = user?.name ?: "Unknown"

// Safe calls
val length = text?.length

// Let for null checks with operations
user?.let { u ->
    println("User: ${u.name}")
    sendWelcomeEmail(u)
}
```

### Collections

```kotlin
// Prefer immutable collections
val list = listOf(1, 2, 3)
val map = mapOf("key" to "value")

// Use collection operations
val evenNumbers = numbers.filter { it % 2 == 0 }
val doubled = numbers.map { it * 2 }
val sum = numbers.sumOf { it }

// Destructuring
val (first, second) = pair
val (id, name, _) = triple
```

### Lambdas

```kotlin
// Use trailing lambda syntax
messages.filter { it.isFromUser }

// Implicit parameter name 'it' for single parameter
items.forEach { println(it) }

// Named parameters for multiple parameters
items.mapIndexed { index, item ->
    "$index: $item"
}

// Prefer method references
items.forEach(::println)
```

## Project Structure

### Package Organization

```
com.localllm.localaichatapp/
├── data/                      # Data Layer
│   ├── local/
│   │   ├── database/         # Room database
│   │   ├── dao/              # Data Access Objects
│   │   └── entity/           # Database entities
│   ├── remote/               # Network/API
│   ├── repository/           # Repository implementations
│   └── mapper/               # Data mappers
├── domain/                    # Domain Layer
│   ├── model/                # Domain models
│   ├── repository/           # Repository interfaces
│   └── usecase/              # Business logic
├── di/                        # Dependency Injection
│   ├── DatabaseModule.kt
│   ├── RepositoryModule.kt
│   └── NetworkModule.kt
└── presentation/              # Presentation Layer
    ├── navigation/           # Navigation
    ├── theme/                # Theming
    ├── home/                 # Home feature
    ├── chat/                 # Chat feature
    └── components/           # Shared UI components
```

### File Naming

```kotlin
// Classes: PascalCase
ChatViewModel.kt
ChatRepository.kt

// Interfaces: PascalCase (no 'I' prefix)
Repository.kt (not IRepository.kt)

// Extensions: ClassNameExt.kt
StringExt.kt
ViewExt.kt

// Constants: PascalCase with 'Constants' suffix
AppConstants.kt
DatabaseConstants.kt
```

## Naming Conventions

### General Rules

- Use meaningful, descriptive names
- Avoid abbreviations (except common ones like `id`, `url`)
- Be consistent with existing code

### Classes and Objects

```kotlin
// Classes: PascalCase, nouns
class ChatViewModel
class UserRepository
class MessageAdapter

// Interfaces: PascalCase, adjectives or nouns
interface Clickable
interface Repository
interface Mapper

// Objects: PascalCase
object AppConfig
object NetworkConstants

// Enums: PascalCase
enum class TaskType {
    CHAT, ASK_IMAGE, ASK_AUDIO, PROMPT_LAB
}
```

### Functions

```kotlin
// Functions: camelCase, verbs
fun sendMessage()
fun calculateTotal()
fun isValid()

// Boolean functions: start with 'is', 'has', 'should', 'can'
fun isUserLoggedIn(): Boolean
fun hasPermission(): Boolean
fun shouldShowDialog(): Boolean
fun canProcess(): Boolean

// Convert/transform functions
fun toEntity(): Entity
fun toDomain(): Domain
fun asFlow(): Flow<T>
```

### Variables and Properties

```kotlin
// Properties: camelCase, nouns
val userName: String
val messageCount: Int
val isLoading: Boolean

// Constants: SCREAMING_SNAKE_CASE
const val MAX_RETRY_COUNT = 3
const val DEFAULT_TIMEOUT_MS = 5000L

// Private backing fields: _prefix
private val _uiState = MutableStateFlow(UiState())
val uiState: StateFlow<UiState> = _uiState.asStateFlow()
```

### Resources

```xml
<!-- Layouts: feature_purpose.xml -->
activity_main.xml
fragment_chat.xml
item_message.xml
dialog_confirmation.xml

<!-- IDs: type_purpose -->
android:id="@+id/button_send"
android:id="@+id/text_message"
android:id="@+id/recycler_messages"

<!-- Drawables: type_name_variant -->
ic_send.xml
ic_send_disabled.xml
bg_button_primary.xml
img_placeholder.png

<!-- Strings: feature_purpose -->
<string name="chat_title">Chat</string>
<string name="error_network">Network error</string>
<string name="button_send">Send</string>

<!-- Dimensions: type_size -->
<dimen name="text_size_large">18sp</dimen>
<dimen name="margin_standard">16dp</dimen>
<dimen name="padding_small">8dp</dimen>

<!-- Colors: purpose_variant -->
<color name="primary">#6200EE</color>
<color name="primary_dark">#3700B3</color>
<color name="text_primary">#000000</color>
```

## Code Organization

### Class Member Order

```kotlin
class ExampleClass {

    // 1. Companion object
    companion object {
        private const val TAG = "ExampleClass"
        const val MAX_COUNT = 100
    }

    // 2. Properties
    private val repository: Repository
    private var count: Int = 0

    // 3. Init blocks
    init {
        // Initialization code
    }

    // 4. Constructor (if separate from class header)
    constructor(repository: Repository) {
        this.repository = repository
    }

    // 5. Override methods
    override fun onCreate() {
        // Implementation
    }

    // 6. Public methods
    fun publicMethod() {
        // Implementation
    }

    // 7. Internal methods
    internal fun internalMethod() {
        // Implementation
    }

    // 8. Protected methods
    protected fun protectedMethod() {
        // Implementation
    }

    // 9. Private methods
    private fun privateMethod() {
        // Implementation
    }

    // 10. Inner classes
    inner class InnerClass {
        // Implementation
    }

    // 11. Nested classes
    class NestedClass {
        // Implementation
    }
}
```

### Import Organization

```kotlin
// Android imports
import android.content.Context
import android.os.Bundle

// AndroidX imports
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.Composable

// Third-party libraries
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// Project imports
import com.localllm.localaichatapp.domain.model.ChatMessage
import com.localllm.localaichatapp.data.repository.ChatRepository
```

Use Android Studio's optimize imports (Ctrl+Alt+O / Cmd+Alt+O).

## Documentation

### KDoc Comments

```kotlin
/**
 * Generates AI response for the given input message.
 *
 * This function uses the specified model to generate a streaming response.
 * The response is emitted as chunks through a Flow for real-time display.
 *
 * @param input The user's message text
 * @param modelId The ID of the AI model to use
 * @param sessionId The current chat session ID
 * @return Flow emitting response chunks with completion status
 * @throws ModelNotFoundException if the specified model doesn't exist
 * @throws InferenceException if AI processing fails
 *
 * @see ChatMessage
 * @see ResponseChunk
 */
suspend fun generateResponse(
    input: String,
    modelId: String,
    sessionId: String
): Flow<ResponseChunk>
```

### Inline Comments

```kotlin
// Use comments to explain WHY, not WHAT
fun processMessage(message: String): String {
    // Remove leading/trailing whitespace to normalize input
    val trimmed = message.trim()

    // Escape special characters to prevent injection attacks
    val escaped = trimmed.replace(regex, "")

    return escaped
}

// Avoid obvious comments
val count = messages.size  // Get the size of messages (BAD - obvious)
```

### TODO Comments

```kotlin
// TODO: Implement real model inference when SDK is integrated
// TODO(username): Optimize this query for large datasets
// FIXME: Handle edge case when session is null
```

## Android-Specific Guidelines

### Activity and Fragment

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LocalAiChatAppTheme {
                AiChatApp()
            }
        }
    }
}
```

### ViewModel

```kotlin
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        loadChatHistory()
    }

    fun sendMessage(message: String) {
        viewModelScope.launch {
            // Implementation
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Cleanup
    }
}
```

### Compose

```kotlin
@Composable
fun ChatScreen(
    uiState: ChatUiState,
    onSendMessage: (String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Chat") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
            }
        )

        MessageList(
            messages = uiState.messages,
            modifier = Modifier.weight(1f)
        )

        MessageInput(
            onSend = onSendMessage,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun ChatScreenPreview() {
    LocalAiChatAppTheme {
        ChatScreen(
            uiState = ChatUiState(),
            onSendMessage = {},
            onNavigateBack = {}
        )
    }
}
```

### Repository

```kotlin
class ChatRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao,
    private val mapper: ChatMessageMapper
) : ChatRepository {

    override suspend fun createSession(
        modelId: String,
        title: String,
        taskType: TaskType
    ): Result<ChatSession> = withContext(Dispatchers.IO) {
        try {
            val entity = ChatSessionEntity(
                id = UUID.randomUUID().toString(),
                modelId = modelId,
                title = title,
                taskType = taskType
            )
            chatDao.insertSession(entity)
            Result.Success(mapper.toDomain(entity))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
```

## Git Practices

### Commit Messages

Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types**:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation
- `style`: Formatting
- `refactor`: Code restructuring
- `test`: Tests
- `chore`: Maintenance

**Examples**:
```
feat(chat): add streaming response support

Implement real-time streaming of AI responses using Flow.
Responses are displayed incrementally as they're generated.

Closes #123

fix(database): resolve migration crash on upgrade

Added missing migration path from v2 to v3.
Includes fallback for corrupt data.

Fixes #456

docs: update installation guide

- Added troubleshooting section
- Clarified system requirements
- Fixed broken links
```

### Branch Naming

```
feature/add-dark-mode
fix/chat-crash-on-empty-message
docs/update-readme
refactor/simplify-repository-layer
test/add-viewmodel-tests
```

## Tools and Automation

### Code Formatting

Use Android Studio's built-in formatter:
- **Format File**: Ctrl+Alt+L (Windows/Linux) / Cmd+Alt+L (macOS)
- **Optimize Imports**: Ctrl+Alt+O (Windows/Linux) / Cmd+Alt+O (macOS)

### Linting

```bash
# Run lint checks
./gradlew lint

# View report
open app/build/reports/lint-results-debug.html
```

### Static Analysis

Consider adding:
- **Detekt**: Kotlin static analysis
- **ktlint**: Kotlin linter
- **Android Lint**: Built-in Android checks

## Summary Checklist

Before submitting code:

- [ ] Code follows Kotlin conventions
- [ ] Naming is clear and consistent
- [ ] Classes have single responsibility
- [ ] Functions are small and focused
- [ ] Comments explain WHY, not WHAT
- [ ] Public APIs are documented
- [ ] Tests are included
- [ ] Lint checks pass
- [ ] Code is formatted
- [ ] Imports are optimized
- [ ] Commit messages follow convention

---

**Remember**: Clean code is a team effort. When in doubt, ask for review or feedback!
