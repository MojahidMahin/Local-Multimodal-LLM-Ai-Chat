package com.localllm.localaichatapp.presentation.askaudio

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.localllm.localaichatapp.domain.model.ChatMessage
import com.localllm.localaichatapp.domain.model.ResponseMetadata
import com.localllm.localaichatapp.presentation.theme.extendedColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AskAudioScreen(
    modelId: String,
    onBackClick: () -> Unit,
    onNavigateToPerformance: () -> Unit,
    viewModel: AskAudioViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Audio file picker launcher
    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.selectAudioFile(it) }
    }

    // Audio permission launcher
    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.onPermissionGranted()
        } else {
            viewModel.onPermissionDenied()
        }
    }

    LaunchedEffect(modelId) {
        viewModel.initializeSession(modelId)
    }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(uiState.messages.size - 1)
            }
        }
    }

    LaunchedEffect(Unit) {
        // Check for audio permission on screen load
        viewModel.checkAudioPermission(context)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        AskAudioTopBar(
            modelName = uiState.modelName,
            sessionTitle = uiState.sessionTitle,
            onBackClick = onBackClick,
            onPerformanceClick = onNavigateToPerformance
        )

        Box(
            modifier = Modifier.weight(1f)
        ) {
            if (uiState.messages.isEmpty() && !uiState.isLoading) {
                EmptyAudioState(
                    hasAudioPermission = uiState.hasAudioPermission,
                    onRequestPermission = { 
                        audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    },
                    onRecordClick = { viewModel.startRecording(context) },
                    onFileClick = { audioPickerLauncher.launch("audio/*") }
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.messages) { message ->
                        AudioMessageItem(
                            message = message,
                            isStreaming = uiState.isStreaming && message == uiState.messages.lastOrNull()
                        )
                    }
                }
            }

            if (uiState.isLoading && uiState.messages.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        AudioInputSection(
            inputText = uiState.inputText,
            selectedAudioUri = uiState.selectedAudioUri,
            isRecording = uiState.isRecording,
            recordingDuration = uiState.recordingDuration,
            hasAudioPermission = uiState.hasAudioPermission,
            onInputChange = viewModel::updateInputText,
            onSendClick = viewModel::sendMessage,
            onRecordClick = if (uiState.isRecording) { 
                { viewModel.stopRecording() }
            } else { 
                { viewModel.startRecording(context) }
            },
            onFileClick = { audioPickerLauncher.launch("audio/*") },
            onRemoveAudio = viewModel::clearSelectedAudio,
            onRequestPermission = { 
                audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            },
            isEnabled = !uiState.isStreaming && (uiState.inputText.isNotBlank() || uiState.selectedAudioUri != null)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AskAudioTopBar(
    modelName: String,
    sessionTitle: String,
    onBackClick: () -> Unit,
    onPerformanceClick: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = sessionTitle.ifBlank { "Ask Audio" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = modelName.ifBlank { "Loading..." },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            IconButton(onClick = onPerformanceClick) {
                Icon(
                    imageVector = Icons.Default.Analytics,
                    contentDescription = "Performance Insights",
                    tint = MaterialTheme.extendedColors.infoColor
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun EmptyAudioState(
    hasAudioPermission: Boolean,
    onRequestPermission: () -> Unit,
    onRecordClick: () -> Unit,
    onFileClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = "Audio Processing",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.extendedColors.audioColor
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "🎤 Ask About Audio",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Record audio or upload files to get AI-powered transcription, analysis, and insights",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        if (!hasAudioPermission) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Microphone permission required",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FilledTonalButton(
                        onClick = onRequestPermission
                    ) {
                        Text("Grant Permission")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onFileClick,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.AudioFile,
                    contentDescription = "Audio File",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Upload File")
            }
            
            FilledTonalButton(
                onClick = if (hasAudioPermission) onRecordClick else onRequestPermission,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Record",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Record Audio")
            }
        }
    }
}

@Composable
private fun AudioMessageItem(
    message: ChatMessage,
    isStreaming: Boolean
) {
    val isUser = when (message) {
        is ChatMessage.User -> true
        is ChatMessage.Assistant -> false
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            MessageAvatar(
                isUser = false,
                modifier = Modifier.padding(end = 8.dp)
            )
        }

        Column(
            modifier = Modifier.widthIn(max = 300.dp),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            AudioMessageBubble(
                message = message,
                isUser = isUser,
                isStreaming = isStreaming
            )

            if (!isUser && message is ChatMessage.Assistant && message.metadata != null) {
                MessageMetadata(
                    metadata = message.metadata,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        if (isUser) {
            MessageAvatar(
                isUser = true,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun MessageAvatar(
    isUser: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(
                if (isUser) MaterialTheme.colorScheme.primary
                else MaterialTheme.extendedColors.audioColor
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isUser) Icons.Default.Person else Icons.Default.Psychology,
            contentDescription = if (isUser) "User" else "AI",
            tint = Color.White,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun AudioMessageBubble(
    message: ChatMessage,
    isUser: Boolean,
    isStreaming: Boolean
) {
    val backgroundColor = if (isUser) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = if (isUser) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = if (isUser) 16.dp else 4.dp,
            bottomEnd = if (isUser) 4.dp else 16.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Show audio player if it's a user message with audio
            if (isUser && message is ChatMessage.User && message.audioUri != null) {
                AudioPlayer(
                    audioUri = message.audioUri,
                    backgroundColor = backgroundColor
                )
                
                if (message.content.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            if (message.content.isNotBlank()) {
                Text(
                    text = when (message) {
                        is ChatMessage.User -> message.content
                        is ChatMessage.Assistant -> message.content
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
            }

            AnimatedVisibility(
                visible = isStreaming,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(textColor.copy(alpha = 0.6f))
                        )
                        if (index < 2) {
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AudioPlayer(
    audioUri: String,
    backgroundColor: Color
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { /* TODO: Play/pause audio */ }
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play audio",
                    tint = Color.White
                )
            }
            
            Text(
                text = "Audio Recording",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            
            Text(
                text = "0:00", // TODO: Show actual duration
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun MessageMetadata(
    metadata: ResponseMetadata,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "⚡ ${metadata.formattedTtft}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "🔄 ${metadata.formattedDecodeSpeed}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "📊 ${metadata.tokensGenerated} tokens",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AudioInputSection(
    inputText: String,
    selectedAudioUri: Uri?,
    isRecording: Boolean,
    recordingDuration: Long,
    hasAudioPermission: Boolean,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onRecordClick: () -> Unit,
    onFileClick: () -> Unit,
    onRemoveAudio: () -> Unit,
    onRequestPermission: () -> Unit,
    isEnabled: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Selected audio preview
            selectedAudioUri?.let { uri ->
                Card(
                    modifier = Modifier.padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AudioFile,
                            contentDescription = "Audio file",
                            tint = MaterialTheme.extendedColors.audioColor
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "Audio file selected",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        
                        IconButton(onClick = onRemoveAudio) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Remove audio",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Recording indicator
            if (isRecording) {
                RecordingIndicator(
                    duration = recordingDuration,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = onInputChange,
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            when {
                                isRecording -> "Recording audio..."
                                selectedAudioUri != null -> "Ask about this audio..."
                                else -> "Ask about audio or record new..."
                            }
                        )
                    },
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 3,
                    enabled = !isRecording,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Audio source buttons
                if (selectedAudioUri == null && !isRecording) {
                    IconButton(
                        onClick = onFileClick,
                        modifier = Modifier
                            .size(48.dp)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.AudioFile,
                            contentDescription = "Audio file",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(4.dp))
                }

                // Record button with animation
                RecordButton(
                    isRecording = isRecording,
                    hasPermission = hasAudioPermission,
                    onClick = if (hasAudioPermission) onRecordClick else onRequestPermission,
                    modifier = Modifier.size(48.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))

                // Send button
                FloatingActionButton(
                    onClick = onSendClick,
                    modifier = Modifier.size(48.dp),
                    containerColor = if (isEnabled) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (isEnabled) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RecordingIndicator(
    duration: Long,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "recording")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "recording_alpha"
    )

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.extendedColors.errorColor.copy(alpha = alpha)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Recording",
                tint = Color.White
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "Recording...",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            
            Text(
                text = formatDuration(duration),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
    }
}

@Composable
private fun RecordButton(
    isRecording: Boolean,
    hasPermission: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "record_button")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRecording) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "record_scale"
    )

    IconButton(
        onClick = onClick,
        modifier = modifier
            .scale(scale)
            .border(
                1.dp,
                if (isRecording) MaterialTheme.extendedColors.errorColor
                else MaterialTheme.colorScheme.outline,
                CircleShape
            )
            .background(
                if (isRecording) MaterialTheme.extendedColors.errorColor
                else Color.Transparent,
                CircleShape
            )
    ) {
        Icon(
            imageVector = if (isRecording) Icons.Default.MicOff else Icons.Default.Mic,
            contentDescription = if (isRecording) "Stop recording" else "Start recording",
            tint = if (isRecording) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatDuration(durationMs: Long): String {
    val seconds = (durationMs / 1000) % 60
    val minutes = (durationMs / (1000 * 60)) % 60
    return String.format("%d:%02d", minutes, seconds)
}