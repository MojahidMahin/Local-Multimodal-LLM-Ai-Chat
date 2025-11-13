# Testing Guide

This guide covers testing practices and procedures for LocalAiChatApp.

## Table of Contents
- [Testing Philosophy](#testing-philosophy)
- [Testing Pyramid](#testing-pyramid)
- [Unit Testing](#unit-testing)
- [Integration Testing](#integration-testing)
- [UI Testing](#ui-testing)
- [Running Tests](#running-tests)
- [Writing Tests](#writing-tests)
- [Test Coverage](#test-coverage)
- [Best Practices](#best-practices)

## Testing Philosophy

### Our Approach

We follow a comprehensive testing strategy to ensure:
- **Reliability**: App works as expected
- **Quality**: High code quality standards
- **Confidence**: Safe refactoring and changes
- **Documentation**: Tests serve as code documentation
- **Regression Prevention**: Catch bugs before users do

### Testing Principles

1. **Test Behavior, Not Implementation**
   - Focus on what code does, not how
   - Tests should survive refactoring

2. **Arrange-Act-Assert (AAA) Pattern**
   ```kotlin
   @Test
   fun testExample() {
       // Arrange: Set up test data
       val input = "test"

       // Act: Execute the code being tested
       val result = function(input)

       // Assert: Verify the outcome
       assertEquals("expected", result)
   }
   ```

3. **One Assertion Per Test (when possible)**
   - Tests should be focused
   - Easy to understand failures

4. **Tests Should Be Fast**
   - Unit tests run in milliseconds
   - Quick feedback loop

5. **Tests Should Be Independent**
   - No shared state between tests
   - Can run in any order

## Testing Pyramid

Our testing strategy follows the test pyramid:

```
         /\
        /  \         E2E Tests (Few)
       /____\        - Full app flows
      /      \       - Critical user journeys
     /        \
    /__________\     Integration Tests (Some)
   /            \    - Multiple components
  /              \   - Database, repos, etc.
 /________________\
/                  \ Unit Tests (Many)
--------------------
- Individual functions
- ViewModels, Use Cases
- Utilities, Mappers
```

### Distribution

- **70%**: Unit Tests
- **20%**: Integration Tests
- **10%**: UI/E2E Tests

## Unit Testing

### What to Unit Test

- **ViewModels**: Business logic, state management
- **Repositories**: Data operations
- **Use Cases**: Domain logic
- **Mappers**: Data transformations
- **Utilities**: Helper functions

### Testing ViewModels

```kotlin
@ExperimentalCoroutinesApi
class ChatViewModelTest {

    // Test rule for coroutines
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Mocks
    private lateinit var chatRepository: ChatRepository
    private lateinit var aiInferenceRepository: AiInferenceRepository
    private lateinit var viewModel: ChatViewModel

    @Before
    fun setup() {
        chatRepository = mockk()
        aiInferenceRepository = mockk()
        viewModel = ChatViewModel(chatRepository, aiInferenceRepository)
    }

    @Test
    fun sendMessage_withValidInput_updatesUiState() = runTest {
        // Arrange
        val userMessage = "Hello"
        val expectedResponse = "Hi there!"

        coEvery {
            aiInferenceRepository.generateResponse(any(), any(), any())
        } returns flowOf(
            ResponseChunk(content = expectedResponse, isComplete = true)
        )

        // Act
        viewModel.sendMessage(userMessage)
        advanceUntilIdle()

        // Assert
        val uiState = viewModel.uiState.value
        assertTrue(uiState.messages.any { it.content == expectedResponse })
        assertFalse(uiState.isLoading)
    }

    @Test
    fun sendMessage_withError_showsErrorState() = runTest {
        // Arrange
        val errorMessage = "Network error"
        coEvery {
            aiInferenceRepository.generateResponse(any(), any(), any())
        } returns flow {
            throw IOException(errorMessage)
        }

        // Act
        viewModel.sendMessage("Hello")
        advanceUntilIdle()

        // Assert
        val uiState = viewModel.uiState.value
        assertNotNull(uiState.error)
        assertTrue(uiState.error?.contains(errorMessage) == true)
    }
}
```

### Testing Repositories

```kotlin
@ExperimentalCoroutinesApi
class ChatRepositoryTest {

    private lateinit var database: ChatDatabase
    private lateinit var chatDao: ChatDao
    private lateinit var repository: ChatRepositoryImpl

    @Before
    fun setup() {
        // Use in-memory database for testing
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            ChatDatabase::class.java
        ).allowMainThreadQueries().build()

        chatDao = database.chatDao()
        repository = ChatRepositoryImpl(chatDao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun createSession_insertsSessionIntoDatabase() = runTest {
        // Arrange
        val modelId = "gemma-2b"
        val title = "Test Chat"

        // Act
        val result = repository.createSession(modelId, title, TaskType.CHAT)

        // Assert
        assertTrue(result is Result.Success)
        val sessions = chatDao.getAllSessions().first()
        assertEquals(1, sessions.size)
        assertEquals(title, sessions[0].title)
    }

    @Test
    fun getSessionById_returnsCorrectSession() = runTest {
        // Arrange
        val session = ChatSessionEntity(
            id = "test-id",
            modelId = "gemma-2b",
            title = "Test Chat",
            taskType = TaskType.CHAT
        )
        chatDao.insertSession(session)

        // Act
        val result = repository.getSessionById("test-id").first()

        // Assert
        assertTrue(result is Result.Success)
        assertEquals(session.title, (result as Result.Success).data?.title)
    }
}
```

### Testing Mappers

```kotlin
class EntityMapperTest {

    private val mapper = ChatMessageMapper()

    @Test
    fun toDomain_convertsEntityCorrectly() {
        // Arrange
        val entity = ChatMessageEntity(
            id = "msg-1",
            sessionId = "session-1",
            content = "Hello",
            isFromUser = true,
            timestamp = 123456789L
        )

        // Act
        val domain = mapper.toDomain(entity)

        // Assert
        assertEquals(entity.id, domain.id)
        assertEquals(entity.content, domain.content)
        assertTrue(domain is ChatMessage.User)
    }

    @Test
    fun toEntity_convertsDomainCorrectly() {
        // Arrange
        val domain = ChatMessage.User(
            id = "msg-1",
            content = "Hello",
            timestamp = 123456789L
        )

        // Act
        val entity = mapper.toEntity(domain, "session-1")

        // Assert
        assertEquals(domain.id, entity.id)
        assertEquals(domain.content, entity.content)
        assertTrue(entity.isFromUser)
    }
}
```

## Integration Testing

### Database Tests

```kotlin
@RunWith(AndroidJUnit4::class)
class ChatDatabaseTest {

    private lateinit var database: ChatDatabase
    private lateinit var chatDao: ChatDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            ChatDatabase::class.java
        ).build()
        chatDao = database.chatDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertAndRetrieveSession() = runTest {
        // Arrange
        val session = ChatSessionEntity(
            id = "test-session",
            modelId = "gemma-2b",
            title = "Test",
            taskType = TaskType.CHAT
        )

        // Act
        chatDao.insertSession(session)
        val retrieved = chatDao.getSessionById("test-session").first()

        // Assert
        assertNotNull(retrieved)
        assertEquals(session.title, retrieved?.title)
    }

    @Test
    fun deleteSession_cascadesDeleteMessages() = runTest {
        // Arrange
        val sessionId = "test-session"
        val session = ChatSessionEntity(
            id = sessionId,
            modelId = "gemma-2b",
            title = "Test",
            taskType = TaskType.CHAT
        )
        val message = ChatMessageEntity(
            id = "msg-1",
            sessionId = sessionId,
            content = "Hello",
            isFromUser = true
        )

        chatDao.insertSession(session)
        chatDao.insertMessage(message)

        // Act
        chatDao.deleteSession(sessionId)

        // Assert
        val messages = chatDao.getMessagesForSession(sessionId).first()
        assertTrue(messages.isEmpty())
    }
}
```

### Repository Integration Tests

```kotlin
@RunWith(AndroidJUnit4::class)
class ChatRepositoryIntegrationTest {

    private lateinit var database: ChatDatabase
    private lateinit var repository: ChatRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            ChatDatabase::class.java
        ).build()

        repository = ChatRepositoryImpl(database.chatDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun fullChatFlow_createsSessionAndMessages() = runTest {
        // Create session
        val sessionResult = repository.createSession(
            modelId = "gemma-2b",
            title = "Integration Test",
            taskType = TaskType.CHAT
        )

        assertTrue(sessionResult is Result.Success)
        val sessionId = (sessionResult as Result.Success).data.id

        // Add messages
        val userMsg = ChatMessage.User(
            id = "msg-1",
            content = "Hello",
            timestamp = System.currentTimeMillis()
        )

        repository.insertMessage(sessionId, userMsg)

        // Verify
        val messages = repository.getMessagesForSession(sessionId).first()
        assertTrue(messages is Result.Success)
        assertEquals(1, (messages as Result.Success).data.size)
    }
}
```

## UI Testing

### Composable Tests

```kotlin
@ExperimentalComposeUiApi
class ChatScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun chatScreen_displaysMessages() {
        // Arrange
        val messages = listOf(
            ChatMessage.User(
                id = "1",
                content = "Hello",
                timestamp = System.currentTimeMillis()
            ),
            ChatMessage.Assistant(
                id = "2",
                content = "Hi there!",
                timestamp = System.currentTimeMillis()
            )
        )

        val uiState = ChatUiState(messages = messages)

        // Act
        composeTestRule.setContent {
            ChatScreen(
                uiState = uiState,
                onSendMessage = {},
                onNavigateBack = {}
            )
        }

        // Assert
        composeTestRule
            .onNodeWithText("Hello")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Hi there!")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun chatScreen_sendButton_triggersCallback() {
        // Arrange
        var messageSent = false
        val uiState = ChatUiState()

        composeTestRule.setContent {
            ChatScreen(
                uiState = uiState,
                onSendMessage = { messageSent = true },
                onNavigateBack = {}
            )
        }

        // Act
        composeTestRule
            .onNodeWithContentDescription("Send message")
            .performClick()

        // Assert
        assertTrue(messageSent)
    }
}
```

### Instrumented Tests

```kotlin
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun launchActivity_showsHomeScreen() {
        // Check if main UI elements are visible
        onView(withText("LocalAiChatApp"))
            .check(matches(isDisplayed()))

        onView(withText("AI Chat"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun clickAiChat_navigatesToChatScreen() {
        // Act
        onView(withText("AI Chat"))
            .perform(click())

        // Assert - verify navigation occurred
        onView(withId(R.id.chat_screen))
            .check(matches(isDisplayed()))
    }
}
```

## Running Tests

### Using Android Studio

1. **Run All Tests**
   - Right-click on `test` or `androidTest` directory
   - Select "Run Tests"

2. **Run Single Test Class**
   - Open test file
   - Click green arrow next to class name
   - Or press `Ctrl+Shift+F10` (Windows/Linux) / `Ctrl+Shift+R` (macOS)

3. **Run Single Test Method**
   - Click green arrow next to method
   - Or place cursor in method and press shortcut

### Using Command Line

```bash
# Run all unit tests
./gradlew test

# Run unit tests for debug variant
./gradlew testDebugUnitTest

# Run specific test class
./gradlew test --tests ChatViewModelTest

# Run specific test method
./gradlew test --tests ChatViewModelTest.sendMessage_withValidInput_updatesUiState

# Run all instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest

# Run instrumented tests for debug
./gradlew connectedDebugAndroidTest

# Run both unit and instrumented tests
./gradlew test connectedAndroidTest
```

### Continuous Testing

```bash
# Run tests continuously (re-runs on file changes)
./gradlew test --continuous

# Run with info logging
./gradlew test --info

# Run with debug logging
./gradlew test --debug
```

## Writing Tests

### Test Structure

```kotlin
class MyFeatureTest {

    // 1. Test Rules
    @get:Rule
    val rule = InstantTaskExecutorRule()

    // 2. Dependencies (mocks, test doubles)
    private lateinit var repository: Repository
    private lateinit var useCase: UseCase

    // 3. Subject under test
    private lateinit var viewModel: ViewModel

    // 4. Setup
    @Before
    fun setup() {
        repository = mockk()
        useCase = UseCaseImpl(repository)
        viewModel = ViewModel(useCase)
    }

    // 5. Teardown
    @After
    fun tearDown() {
        // Clean up resources
    }

    // 6. Tests
    @Test
    fun featureName_condition_expectedResult() {
        // Arrange
        // Act
        // Assert
    }
}
```

### Naming Conventions

**Test Class Names**:
```kotlin
// Feature being tested + "Test"
class ChatViewModelTest
class ChatRepositoryTest
class MessageMapperTest
```

**Test Method Names**:
```kotlin
// methodName_condition_expectedResult
fun sendMessage_withValidInput_returnsSuccess()
fun sendMessage_withEmptyInput_returnsError()
fun getSession_whenNotFound_returnsNull()
```

### Using Test Doubles

```kotlin
// Mock - Full mock object
val repository = mockk<ChatRepository>()
coEvery { repository.getSession(any()) } returns Result.Success(session)

// Spy - Partial mock (real object with some mocked methods)
val repository = spyk(ChatRepositoryImpl(dao))
coEvery { repository.someMethod() } returns mockData

// Fake - Working implementation for testing
class FakeChatRepository : ChatRepository {
    private val sessions = mutableListOf<ChatSession>()

    override suspend fun createSession(...): Result<ChatSession> {
        val session = ChatSession(...)
        sessions.add(session)
        return Result.Success(session)
    }
}

// Stub - Returns predetermined values
val repository = object : ChatRepository {
    override suspend fun getSession(id: String) = Result.Success(testSession)
}
```

## Test Coverage

### Generating Coverage Reports

```bash
# Generate coverage report
./gradlew jacocoTestReport

# View report
open app/build/reports/jacoco/test/html/index.html
```

### Coverage Goals

- **Overall**: 70%+ coverage
- **ViewModels**: 80%+ coverage
- **Repositories**: 80%+ coverage
- **Use Cases**: 90%+ coverage
- **Utilities**: 90%+ coverage

### What to Focus On

**High Priority** (must test):
- Business logic
- Data transformations
- Error handling
- Edge cases

**Medium Priority** (should test):
- UI logic in ViewModels
- Navigation logic
- Complex calculations

**Low Priority** (can skip):
- Simple getters/setters
- Data classes
- Framework code

## Best Practices

### General Guidelines

1. **Test One Thing**
   ```kotlin
   // Good
   @Test
   fun sendMessage_updatesMessageList() { }

   @Test
   fun sendMessage_clearsInputField() { }

   // Bad
   @Test
   fun sendMessage_doesEverything() { }
   ```

2. **Use Descriptive Names**
   ```kotlin
   // Good
   val userMessage = "Hello, world!"
   val expectedResponse = "Hi there!"

   // Bad
   val msg = "Hello, world!"
   val res = "Hi there!"
   ```

3. **Avoid Test Interdependence**
   ```kotlin
   // Good - Each test is independent
   @Test
   fun test1() {
       val data = createTestData()
       // test with data
   }

   @Test
   fun test2() {
       val data = createTestData()
       // test with data
   }

   // Bad - Tests share state
   val sharedData = createTestData()

   @Test
   fun test1() {
       sharedData.modify()
   }

   @Test
   fun test2() {
       // Depends on test1
   }
   ```

4. **Keep Tests Fast**
   - Use in-memory databases
   - Mock external dependencies
   - Avoid Thread.sleep()

5. **Test Edge Cases**
   ```kotlin
   @Test
   fun calculate_withZero_returnsZero()

   @Test
   fun calculate_withNegative_throwsException()

   @Test
   fun calculate_withMaxValue_handlesOverflow()
   ```

### Android-Specific

1. **Use Robolectric for Android Framework Testing**
   ```kotlin
   @RunWith(RobolectricTestRunner::class)
   class AndroidUtilTest {
       @Test
       fun contextTest() {
           val context = ApplicationProvider.getApplicationContext<Context>()
           // Test code using context
       }
   }
   ```

2. **Test Coroutines Properly**
   ```kotlin
   @ExperimentalCoroutinesApi
   @Test
   fun testCoroutine() = runTest {
       // Launch coroutines
       viewModel.doSomething()

       // Wait for completion
       advanceUntilIdle()

       // Assert
       assertTrue(viewModel.isComplete)
   }
   ```

3. **Test LiveData/StateFlow**
   ```kotlin
   @Test
   fun testStateFlow() = runTest {
       val values = mutableListOf<State>()

       val job = launch {
           viewModel.uiState.collect {
               values.add(it)
           }
       }

       viewModel.doSomething()
       advanceUntilIdle()

       assertEquals(expectedState, values.last())
       job.cancel()
   }
   ```

## Resources

- [Android Testing Documentation](https://developer.android.com/training/testing)
- [JUnit 4 Documentation](https://junit.org/junit4/)
- [MockK Documentation](https://mockk.io/)
- [Compose Testing](https://developer.android.com/jetpack/compose/testing)
- [Testing Coroutines](https://developer.android.com/kotlin/coroutines/test)

---

**Remember**: Good tests are an investment in code quality and long-term maintainability.
