package com.localllm.localaichatapp.presentation.models

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.localllm.localaichatapp.domain.model.AiModel
import com.localllm.localaichatapp.domain.model.ModelFeature
import com.localllm.localaichatapp.presentation.models.components.ModelCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelManagementScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ModelManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = "AI Models",
                    fontWeight = FontWeight.Medium
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = { viewModel.onEvent(ModelManagementEvent.RefreshModels) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh"
                    )
                }
            }
        )
        
        // Error display
        uiState.error?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    TextButton(
                        onClick = { viewModel.onEvent(ModelManagementEvent.ClearError) }
                    ) {
                        Text("Dismiss")
                    }
                }
            }
        }
        
        // Loading state
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator()
                    Text("Loading models...")
                }
            }
        }
        
        // Models list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (uiState.availableModels.isEmpty() && !uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "📦",
                                style = MaterialTheme.typography.displayMedium
                            )
                            Text(
                                text = "No models available",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                text = "Please check your connection and try again",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            } else {
                items(uiState.availableModels) { model ->
                    ModelCard(
                        model = model,
                        downloadProgress = uiState.downloadingModels[model.id],
                        onDownload = { viewModel.onEvent(ModelManagementEvent.DownloadModel(model.id)) },
                        onInitialize = { viewModel.onEvent(ModelManagementEvent.InitializeModel(model.id)) },
                        onDelete = { viewModel.onEvent(ModelManagementEvent.ShowDeleteConfirmation(model)) }
                    )
                }
            }
        }
    }
    
    // Delete confirmation dialog
    if (uiState.showDeleteConfirmation) {
        val modelToDelete = uiState.selectedModelForDeletion
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(ModelManagementEvent.HideDeleteConfirmation) },
            title = { Text("Delete Model") },
            text = {
                Text(
                    "Are you sure you want to delete ${modelToDelete?.name}? " +
                    "This will free up ${formatFileSize(modelToDelete?.modelSize ?: 0L)} of storage."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.onEvent(ModelManagementEvent.ConfirmDeletion) }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.onEvent(ModelManagementEvent.HideDeleteConfirmation) }
                ) {
                    Text("Cancel")
                }
            }
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
private fun PreviewModelManagementScreen() {
    MaterialTheme {
        Surface {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Model Management Screen Preview")
            }
        }
    }
}