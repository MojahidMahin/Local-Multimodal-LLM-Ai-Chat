package com.localllm.localaichatapp.presentation.models.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.localllm.localaichatapp.domain.model.Model
import com.localllm.localaichatapp.domain.model.TaskType

@Composable
fun ModelCard(
    model: Model,
    downloadProgress: Float?,
    onDownload: () -> Unit,
    onInitialize: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Model header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = model.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = formatFileSize(model.size),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                ModelStatusChip(
                    isDownloaded = model.isDownloaded,
                    isInitialized = model.isAvailable,
                    isDownloading = downloadProgress != null
                )
            }
            
            // Model description
            Text(
                text = model.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            // Tasks
            if (model.supportedTasks.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    model.supportedTasks.take(3).forEach { task ->
                        TaskChip(task = task)
                    }
                    if (model.supportedTasks.size > 3) {
                        Text(
                            text = "+${model.supportedTasks.size - 3}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }
            
            // Download progress
            downloadProgress?.let { progress ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Downloading...",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when {
                    downloadProgress != null -> {
                        // Currently downloading - show disabled button
                        Button(
                            onClick = { },
                            enabled = false,
                            modifier = Modifier.weight(1f)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Downloading...")
                        }
                    }
                    
                    !model.isDownloaded -> {
                        // Not downloaded - show download button
                        Button(
                            onClick = onDownload,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Download")
                        }
                    }
                    
                    !model.isAvailable -> {
                        // Downloaded but not initialized
                        Button(
                            onClick = onInitialize,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Initialize")
                        }
                        
                        OutlinedButton(onClick = onDelete) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    
                    else -> {
                        // Ready to use
                        Text(
                            text = "Ready",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                        
                        OutlinedButton(onClick = onDelete) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModelStatusChip(
    isDownloaded: Boolean,
    isInitialized: Boolean,
    isDownloading: Boolean
) {
    val (color, text) = when {
        isDownloading -> MaterialTheme.colorScheme.tertiary to "Downloading"
        isInitialized -> MaterialTheme.colorScheme.primary to "Ready"
        isDownloaded -> MaterialTheme.colorScheme.secondary to "Downloaded"
        else -> MaterialTheme.colorScheme.outline to "Not Downloaded"
    }
    
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun TaskChip(
    task: TaskType
) {
    val text = when (task) {
        TaskType.CHAT -> "Chat"
        TaskType.ASK_IMAGE -> "Images"
        TaskType.ASK_AUDIO -> "Audio"
        TaskType.PROMPT_LAB -> "Prompts"
    }
    
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatFileSize(bytes: Long): String {
    val gb = bytes / 1_000_000_000.0
    return when {
        gb >= 1.0 -> String.format("%.1f GB", gb)
        else -> {
            val mb = bytes / 1_000_000.0
            String.format("%.0f MB", mb)
        }
    }
}

@Preview
@Composable
private fun PreviewModelCard() {
    MaterialTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            // Not downloaded model
            ModelCard(
                model = Model(
                    id = "gemma-2b",
                    name = "Gemma 2B",
                    displayName = "Gemma 2B",
                    description = "Small and efficient model for basic conversational tasks",
                    author = "Google",
                    size = 1_500_000_000L,
                    downloadUrl = "https://example.com/gemma-2b",
                    supportedTasks = listOf(TaskType.CHAT, TaskType.PROMPT_LAB)
                ),
                downloadProgress = null,
                onDownload = {},
                onInitialize = {},
                onDelete = {}
            )
            
            // Downloading model
            ModelCard(
                model = Model(
                    id = "gemma-7b",
                    name = "Gemma 7B",
                    displayName = "Gemma 7B",
                    description = "Larger model with better performance and image understanding",
                    author = "Google",
                    size = 4_200_000_000L,
                    downloadUrl = "https://example.com/gemma-7b",
                    supportedTasks = listOf(TaskType.CHAT, TaskType.ASK_IMAGE, TaskType.PROMPT_LAB)
                ),
                downloadProgress = 0.65f,
                onDownload = {},
                onInitialize = {},
                onDelete = {}
            )
            
            // Ready model
            ModelCard(
                model = Model(
                    id = "phi-3-mini",
                    name = "Phi-3 Mini",
                    displayName = "Phi-3 Mini",
                    description = "Microsoft's efficient small model",
                    author = "Microsoft",
                    size = 2_100_000_000L,
                    downloadUrl = "https://example.com/phi-3-mini",
                    isDownloaded = true,
                    supportedTasks = listOf(TaskType.CHAT, TaskType.PROMPT_LAB)
                ),
                downloadProgress = null,
                onDownload = {},
                onInitialize = {},
                onDelete = {}
            )
        }
    }
}