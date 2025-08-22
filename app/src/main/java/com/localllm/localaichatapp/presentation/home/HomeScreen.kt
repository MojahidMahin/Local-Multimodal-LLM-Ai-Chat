package com.localllm.localaichatapp.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.localllm.localaichatapp.domain.model.TaskType
import com.localllm.localaichatapp.presentation.theme.LocalAiChatAppTheme
import com.localllm.localaichatapp.presentation.theme.extendedColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToTask: (taskType: String, modelId: String) -> Unit,
    onNavigateToModelManager: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // App header
        HomeHeader(
            onNavigateToModelManager = onNavigateToModelManager,
            onNavigateToSettings = onNavigateToSettings
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Welcome section
        WelcomeSection()
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Task selection cards
        Text(
            text = "Choose Your AI Experience",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        val taskItems = getTaskItems()
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(taskItems) { taskItem ->
                TaskCard(
                    taskItem = taskItem,
                    onTaskClick = { taskType ->
                        // For demo, use a default model ID
                        // In real implementation, show model selector or use last used model
                        onNavigateToTask(taskType, "gemma-2b")
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Recent sessions (if any)
        if (uiState.recentSessions.isNotEmpty()) {
            RecentSessionsSection(
                sessions = uiState.recentSessions,
                onSessionClick = { sessionId, taskType ->
                    // Navigate to specific session
                    onNavigateToTask(taskType, "")  // Will be handled by session restoration
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeHeader(
    onNavigateToModelManager: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Local AI Chat",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "AI Gallery Experience",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = onNavigateToModelManager) {
                Icon(
                    imageVector = Icons.Default.Storage,
                    contentDescription = "Model Manager",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            IconButton(onClick = onNavigateToSettings) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun WelcomeSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🤖 Welcome to AI Gallery",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Experience the future of on-device AI with multi-modal capabilities, performance monitoring, and complete privacy.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskCard(
    taskItem: TaskItem,
    onTaskClick: (String) -> Unit
) {
    Card(
        onClick = { onTaskClick(taskItem.taskType) },
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = taskItem.color.copy(alpha = 0.1f)
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = taskItem.icon,
                contentDescription = taskItem.title,
                tint = taskItem.color,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = taskItem.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = taskItem.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun RecentSessionsSection(
    sessions: List<com.localllm.localaichatapp.domain.model.ChatSession>,
    onSessionClick: (String, String) -> Unit
) {
    Column {
        Text(
            text = "Recent Sessions",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        sessions.take(3).forEach { session ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                onClick = { onSessionClick(session.id, session.taskType.name) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = getTaskIcon(session.taskType.name),
                        contentDescription = session.taskType.name,
                        tint = getTaskColor(session.taskType.name),
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = session.title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = session.preview,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

// Helper functions and data classes
private data class TaskItem(
    val taskType: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: androidx.compose.ui.graphics.Color
)


@Composable
private fun getTaskItems(): List<TaskItem> {
    val extendedColors = MaterialTheme.extendedColors
    return listOf(
        TaskItem(
            taskType = "CHAT",
            title = "AI Chat",
            description = "Multi-turn conversational AI with conversation history",
            icon = Icons.Default.Chat,
            color = extendedColors.chatColor
        ),
        TaskItem(
            taskType = "ASK_IMAGE",
            title = "Ask Image",
            description = "Upload images and get detailed analysis and insights",
            icon = Icons.Default.Image,
            color = extendedColors.imageColor
        ),
        TaskItem(
            taskType = "ASK_AUDIO",
            title = "Ask Audio",
            description = "Process audio files with transcription and analysis",
            icon = Icons.Default.Mic,
            color = extendedColors.audioColor
        ),
        TaskItem(
            taskType = "PROMPT_LAB",
            title = "Prompt Lab",
            description = "Single-turn AI interactions with customizable templates",
            icon = Icons.Default.Psychology,
            color = extendedColors.promptLabColor
        )
    )
}

@Composable
private fun getTaskIcon(taskType: String): ImageVector {
    return when (taskType) {
        "CHAT" -> Icons.Default.Chat
        "ASK_IMAGE" -> Icons.Default.Image
        "ASK_AUDIO" -> Icons.Default.Mic
        "PROMPT_LAB" -> Icons.Default.Psychology
        else -> Icons.Default.Chat
    }
}

@Composable
private fun getTaskColor(taskType: String): androidx.compose.ui.graphics.Color {
    val extendedColors = MaterialTheme.extendedColors
    return when (taskType) {
        "CHAT" -> extendedColors.chatColor
        "ASK_IMAGE" -> extendedColors.imageColor
        "ASK_AUDIO" -> extendedColors.audioColor
        "PROMPT_LAB" -> extendedColors.promptLabColor
        else -> extendedColors.chatColor
    }
}