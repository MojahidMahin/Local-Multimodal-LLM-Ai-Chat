package com.localllm.localaichatapp.presentation.askaudio

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localllm.localaichatapp.domain.model.ChatMessage
import com.localllm.localaichatapp.domain.model.ChatSession
import com.localllm.localaichatapp.domain.model.Model
import com.localllm.localaichatapp.domain.model.TaskType
import com.localllm.localaichatapp.domain.repository.AiInferenceRepository
import com.localllm.localaichatapp.domain.repository.ChatRepository
import com.localllm.localaichatapp.domain.repository.ModelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import javax.inject.Inject

data class AskAudioUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val selectedAudioUri: Uri? = null,
    val isRecording: Boolean = false,
    val recordingDuration: Long = 0L,
    val hasAudioPermission: Boolean = false,
    val isLoading: Boolean = false,
    val isStreaming: Boolean = false,
    val modelName: String = "",
    val sessionTitle: String = "",
    val currentSession: ChatSession? = null,
    val currentModel: Model? = null,
    val error: String? = null
)

@HiltViewModel
class AskAudioViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val modelRepository: ModelRepository,
    private val aiInferenceRepository: AiInferenceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AskAudioUiState())
    val uiState: StateFlow<AskAudioUiState> = _uiState.asStateFlow()

    private var currentSessionId: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var currentAudioFile: File? = null
    private var recordingStartTime: Long = 0L

    fun initializeSession(modelId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // Get model information
                val model = modelRepository.getModelById(modelId).first()
                if (model == null) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Model not found"
                        )
                    }
                    return@launch
                }

                // Create new session for audio analysis
                val session = chatRepository.createSession(
                    modelId = modelId,
                    title = "Audio Analysis",
                    taskType = TaskType.ASK_AUDIO
                )
                currentSessionId = session.id

                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        currentSession = session,
                        currentModel = model,
                        modelName = model.name,
                        sessionTitle = session.title
                    )
                }

                // Load existing messages if any
                loadMessages(session.id)

            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to initialize session: ${e.message}"
                    )
                }
            }
        }
    }

    private fun loadMessages(sessionId: String) {
        viewModelScope.launch {
            chatRepository.observeMessages(sessionId)
                .collect { messages ->
                    _uiState.update { it.copy(messages = messages) }
                }
        }
    }

    fun checkAudioPermission(context: Context) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        
        _uiState.update { it.copy(hasAudioPermission = hasPermission) }
    }

    fun onPermissionGranted() {
        _uiState.update { it.copy(hasAudioPermission = true) }
    }

    fun onPermissionDenied() {
        _uiState.update { 
            it.copy(
                hasAudioPermission = false,
                error = "Microphone permission is required for audio recording"
            )
        }
    }

    fun updateInputText(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun selectAudioFile(uri: Uri) {
        _uiState.update { it.copy(selectedAudioUri = uri) }
    }

    fun clearSelectedAudio() {
        _uiState.update { it.copy(selectedAudioUri = null) }
    }

    fun startRecording(context: Context) {
        if (!_uiState.value.hasAudioPermission) {
            _uiState.update { it.copy(error = "Microphone permission required") }
            return
        }

        viewModelScope.launch {
            try {
                // Create audio file
                val audioFile = File.createTempFile("audio_recording_", ".m4a")
                currentAudioFile = audioFile

                // Initialize MediaRecorder
                mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    MediaRecorder(context)
                } else {
                    @Suppress("DEPRECATION")
                    MediaRecorder()
                }.apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    setOutputFile(audioFile.absolutePath)
                    prepare()
                    start()
                }

                recordingStartTime = System.currentTimeMillis()
                _uiState.update { it.copy(isRecording = true, recordingDuration = 0L) }

                // Start duration timer
                startRecordingTimer()

            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isRecording = false,
                        error = "Failed to start recording: ${e.message}"
                    )
                }
                cleanup()
            }
        }
    }

    fun stopRecording() {
        viewModelScope.launch {
            try {
                mediaRecorder?.apply {
                    stop()
                    release()
                }
                mediaRecorder = null

                currentAudioFile?.let { file ->
                    if (file.exists() && file.length() > 0) {
                        _uiState.update { 
                            it.copy(
                                isRecording = false,
                                selectedAudioUri = Uri.fromFile(file),
                                recordingDuration = 0L
                            )
                        }
                    } else {
                        _uiState.update { 
                            it.copy(
                                isRecording = false,
                                error = "Recording failed - no audio data"
                            )
                        }
                        cleanup()
                    }
                }

            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isRecording = false,
                        error = "Failed to stop recording: ${e.message}"
                    )
                }
                cleanup()
            }
        }
    }

    private fun startRecordingTimer() {
        viewModelScope.launch {
            while (_uiState.value.isRecording) {
                val duration = System.currentTimeMillis() - recordingStartTime
                _uiState.update { it.copy(recordingDuration = duration) }
                delay(100) // Update every 100ms
            }
        }
    }

    fun sendMessage() {
        val currentState = _uiState.value
        val inputText = currentState.inputText.trim()
        val audioUri = currentState.selectedAudioUri
        val sessionId = currentSessionId

        if ((inputText.isBlank() && audioUri == null) || sessionId == null || currentState.isStreaming) {
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { 
                    it.copy(
                        inputText = "",
                        selectedAudioUri = null,
                        isStreaming = true
                    )
                }

                // Create and save user message with audio
                val userMessage = ChatMessage.User(
                    id = UUID.randomUUID().toString(),
                    sessionId = sessionId,
                    content = inputText.ifBlank { "Analyze this audio" },
                    timestamp = System.currentTimeMillis(),
                    audioUri = audioUri?.toString()
                )

                chatRepository.addMessage(sessionId, userMessage)

                // Update session title if it's the first message
                if (currentState.messages.isEmpty()) {
                    val title = if (inputText.isNotBlank()) {
                        inputText.take(50).trim()
                    } else {
                        "Audio Analysis"
                    }
                    
                    val updatedSession = currentState.currentSession?.copy(
                        title = title,
                        updatedAt = System.currentTimeMillis()
                    )
                    updatedSession?.let { 
                        chatRepository.updateSession(it)
                        _uiState.update { state -> 
                            state.copy(
                                currentSession = updatedSession,
                                sessionTitle = updatedSession.title
                            )
                        }
                    }
                }

                // Generate AI response for audio analysis
                generateAudioAnalysis(sessionId, inputText, audioUri)

            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isStreaming = false,
                        error = "Failed to send message: ${e.message}"
                    )
                }
            }
        }
    }

    private suspend fun generateAudioAnalysis(sessionId: String, userInput: String, audioUri: Uri?) {
        try {
            val model = _uiState.value.currentModel ?: return
            val conversationHistory = _uiState.value.messages

            // Create initial assistant message
            val assistantMessageId = UUID.randomUUID().toString()
            val initialAssistantMessage = ChatMessage.Assistant(
                id = assistantMessageId,
                sessionId = sessionId,
                content = "",
                timestamp = System.currentTimeMillis(),
                metadata = null
            )

            chatRepository.addMessage(sessionId, initialAssistantMessage)

            // Generate response with audio analysis
            val responseFlow = aiInferenceRepository.generateAudioAnalysis(
                model = model,
                messages = conversationHistory,
                userInput = userInput.ifBlank { "Analyze this audio" },
                audioUri = audioUri
            )

            var fullContent = ""
            responseFlow.collect { chunk ->
                fullContent += chunk.content
                
                // Update the assistant message with accumulated content
                val updatedMessage = initialAssistantMessage.copy(
                    content = fullContent,
                    metadata = chunk.metadata
                )
                
                chatRepository.updateMessage(updatedMessage.id, updatedMessage)
            }

            _uiState.update { it.copy(isStreaming = false) }

        } catch (e: Exception) {
            _uiState.update { 
                it.copy(
                    isStreaming = false,
                    error = "Failed to analyze audio: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearSession() {
        viewModelScope.launch {
            currentSessionId?.let { sessionId ->
                try {
                    chatRepository.clearMessages(sessionId)
                    _uiState.update { 
                        it.copy(
                            messages = emptyList(),
                            sessionTitle = "Audio Analysis"
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update { 
                        it.copy(error = "Failed to clear session: ${e.message}")
                    }
                }
            }
        }
    }

    private fun cleanup() {
        mediaRecorder?.release()
        mediaRecorder = null
        currentAudioFile?.delete()
        currentAudioFile = null
    }

    override fun onCleared() {
        super.onCleared()
        
        // Clean up recording resources
        cleanup()
        
        // Save session state before clearing
        currentSessionId?.let { sessionId ->
            viewModelScope.launch {
                try {
                    val currentSession = _uiState.value.currentSession?.copy(
                        updatedAt = System.currentTimeMillis()
                    )
                    currentSession?.let { 
                        chatRepository.updateSession(it)
                    }
                } catch (e: Exception) {
                    // Log error but don't crash
                }
            }
        }
    }
}