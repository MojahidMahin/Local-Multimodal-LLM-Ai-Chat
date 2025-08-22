package com.localllm.localaichatapp.presentation.askimage

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.localllm.localaichatapp.domain.model.ChatMessage
import com.localllm.localaichatapp.domain.model.ResponseMetadata
import com.localllm.localaichatapp.presentation.theme.extendedColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AskImageScreen(
    modelId: String,
    onBackClick: () -> Unit,
    onNavigateToPerformance: () -> Unit,
    viewModel: AskImageViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.selectImage(it) }
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            viewModel.onCameraImageCaptured()
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

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        AskImageTopBar(
            modelName = uiState.modelName,
            sessionTitle = uiState.sessionTitle,
            onBackClick = onBackClick,
            onPerformanceClick = onNavigateToPerformance
        )

        Box(
            modifier = Modifier.weight(1f)
        ) {
            if (uiState.messages.isEmpty() && !uiState.isLoading) {
                EmptyImageState(
                    onGalleryClick = { imagePickerLauncher.launch("image/*") },
                    onCameraClick = { 
                        val uri = viewModel.createImageUri(context)
                        cameraLauncher.launch(uri)
                    }
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.messages) { message ->
                        ImageMessageItem(
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

        ImageInputSection(
            inputText = uiState.inputText,
            selectedImageUri = uiState.selectedImageUri,
            onInputChange = viewModel::updateInputText,
            onSendClick = viewModel::sendMessage,
            onGalleryClick = { imagePickerLauncher.launch("image/*") },
            onCameraClick = { 
                val uri = viewModel.createImageUri(context)
                cameraLauncher.launch(uri)
            },
            onRemoveImage = viewModel::clearSelectedImage,
            isEnabled = !uiState.isStreaming && (uiState.inputText.isNotBlank() || uiState.selectedImageUri != null)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AskImageTopBar(
    modelName: String,
    sessionTitle: String,
    onBackClick: () -> Unit,
    onPerformanceClick: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = sessionTitle.ifBlank { "Ask Image" },
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
private fun EmptyImageState(
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Image,
            contentDescription = "Image Analysis",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.extendedColors.imageColor
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "📸 Ask About Images",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Upload an image from gallery or take a photo to get AI-powered analysis and insights",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onGalleryClick,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Gallery",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Gallery")
            }
            
            FilledTonalButton(
                onClick = onCameraClick,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Camera",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Camera")
            }
        }
    }
}

@Composable
private fun ImageMessageItem(
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
            ImageMessageBubble(
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
                else MaterialTheme.extendedColors.imageColor
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
private fun ImageMessageBubble(
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
            // Show image if it's a user message with image
            if (isUser && message is ChatMessage.User && message.imageUri != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(message.imageUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "User image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
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
private fun ImageInputSection(
    inputText: String,
    selectedImageUri: Uri?,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
    onRemoveImage: () -> Unit,
    isEnabled: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Selected image preview
            selectedImageUri?.let { uri ->
                Card(
                    modifier = Modifier.padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(uri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Selected image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        
                        IconButton(
                            onClick = onRemoveImage,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .background(
                                    Color.Black.copy(alpha = 0.5f),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt, // Using close icon would be better
                                contentDescription = "Remove image",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
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
                            if (selectedImageUri != null) "Ask about this image..." 
                            else "Ask about an image..."
                        )
                    },
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Image source buttons
                if (selectedImageUri == null) {
                    IconButton(
                        onClick = onGalleryClick,
                        modifier = Modifier
                            .size(48.dp)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Gallery",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    IconButton(
                        onClick = onCameraClick,
                        modifier = Modifier
                            .size(48.dp)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Camera",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(4.dp))
                }

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