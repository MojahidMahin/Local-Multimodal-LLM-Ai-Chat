package com.localllm.localaichatapp.presentation.modelmanager

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.localllm.localaichatapp.domain.model.Model
import com.localllm.localaichatapp.domain.model.ModelStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelManagerScreen(
    onNavigateBack: () -> Unit,
    viewModel: ModelManagerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // File picker launcher for local model selection
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.importLocalModel(it) }
    }
    
    LaunchedEffect(Unit) {
        viewModel.loadModels()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Model Manager") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshModels() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    
                    // Add Model Menu
                    var showAddMenu by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { showAddMenu = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Model")
                        }
                        DropdownMenu(
                            expanded = showAddMenu,
                            onDismissRequest = { showAddMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Download from Repository") },
                                onClick = {
                                    showAddMenu = false
                                    viewModel.showAddModelDialog()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.CloudDownload, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Import from Local Storage") },
                                onClick = {
                                    showAddMenu = false
                                    // Launch file picker with filter for common model formats
                                    filePickerLauncher.launch("*/*")
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Folder, contentDescription = null)
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Storage Usage Summary
                StorageUsageCard(
                    totalStorage = uiState.totalStorage,
                    usedStorage = uiState.usedStorage,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Filter Tabs
                ModelFilterTabs(
                    selectedFilter = uiState.selectedFilter,
                    onFilterSelected = viewModel::selectFilter,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Models List
                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    
                    uiState.filteredModels.isEmpty() -> {
                        EmptyModelsState(
                            filter = uiState.selectedFilter,
                            onAddModel = { viewModel.showAddModelDialog() },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    
                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(uiState.filteredModels) { model ->
                                ModelCard(
                                    model = model,
                                    onDownload = { viewModel.downloadModel(model.id) },
                                    onPause = { viewModel.pauseDownload(model.id) },
                                    onResume = { viewModel.resumeDownload(model.id) },
                                    onCancel = { viewModel.cancelDownload(model.id) },
                                    onDelete = { viewModel.deleteModel(model.id) },
                                    onSetPrimary = { viewModel.setPrimaryModel(model.id) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
            
            // Show message (error or success) via Snackbar
            uiState.error?.let { message ->
                LaunchedEffect(message) {
                    // Auto-dismiss after 3 seconds
                    kotlinx.coroutines.delay(3000)
                    viewModel.clearError()
                }
                
                // Display message at bottom
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (message.contains("successfully")) {
                            Color(0xFF4CAF50) // Green for success
                        } else {
                            MaterialTheme.colorScheme.errorContainer
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (message.contains("successfully")) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = null,
                            tint = if (message.contains("successfully")) Color.White else MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = message,
                            color = if (message.contains("successfully")) Color.White else MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewModel.clearError() }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = if (message.contains("successfully")) Color.White else MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Add Model Dialog
    if (uiState.showAddModelDialog) {
        AddModelDialog(
            onDismiss = { viewModel.hideAddModelDialog() },
            onAddModel = { url, name -> viewModel.addCustomModel(url, name) }
        )
    }
    
    // Delete Confirmation Dialog
    uiState.modelToDelete?.let { model ->
        DeleteModelDialog(
            model = model,
            onConfirm = { viewModel.confirmDeleteModel() },
            onDismiss = { viewModel.cancelDeleteModel() }
        )
    }
}

@Composable
private fun StorageUsageCard(
    totalStorage: Long,
    usedStorage: Long,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Storage Usage",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = "${formatFileSize(usedStorage)} / ${formatFileSize(totalStorage)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            val usagePercentage = if (totalStorage > 0) usedStorage.toFloat() / totalStorage else 0f
            
            LinearProgressIndicator(
                progress = usagePercentage,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = when {
                    usagePercentage > 0.9f -> MaterialTheme.colorScheme.error
                    usagePercentage > 0.7f -> Color(0xFFFF9800)
                    else -> MaterialTheme.colorScheme.primary
                }
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "${(usagePercentage * 100).toInt()}% used",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun ModelFilterTabs(
    selectedFilter: ModelFilter,
    onFilterSelected: (ModelFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    ScrollableTabRow(
        selectedTabIndex = ModelFilter.values().indexOf(selectedFilter),
        modifier = modifier,
        edgePadding = 0.dp
    ) {
        ModelFilter.values().forEach { filter ->
            Tab(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                text = {
                    Text(
                        text = filter.displayName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    }
}

@Composable
private fun ModelCard(
    model: Model,
    onDownload: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onCancel: () -> Unit,
    onDelete: () -> Unit,
    onSetPrimary: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Model Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = model.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        if (model.isPrimary) {
                            Spacer(modifier = Modifier.width(8.dp))
                            AssistChip(
                                onClick = { },
                                label = { Text("Primary") },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    labelColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier.height(24.dp)
                            )
                        }
                        
                        // Show "Local" indicator for locally imported models
                        if (model.id.startsWith("local_")) {
                            Spacer(modifier = Modifier.width(8.dp))
                            AssistChip(
                                onClick = { },
                                label = { 
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Folder,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Local")
                                    }
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    labelColor = MaterialTheme.colorScheme.onSecondary
                                ),
                                modifier = Modifier.height(24.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = model.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Model Info
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ModelInfoChip(
                            icon = Icons.Default.Storage,
                            text = formatFileSize(model.size)
                        )
                        ModelInfoChip(
                            icon = Icons.Default.Speed,
                            text = model.parameters?.architecture ?: "Unknown"
                        )
                        if (model.supportedTasks.isNotEmpty()) {
                            ModelInfoChip(
                                icon = Icons.Default.Category,
                                text = model.supportedTasks.first().name
                            )
                        }
                    }
                }
                
                // Status Indicator
                ModelStatusIndicator(
                    status = model.status,
                    downloadProgress = model.downloadProgress
                )
            }
            
            // Download Progress (if downloading)
            if (model.status in listOf(ModelStatus.DOWNLOADING, ModelStatus.PAUSED)) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = when (model.status) {
                                ModelStatus.DOWNLOADING -> "Downloading..."
                                ModelStatus.PAUSED -> "Paused"
                                else -> ""
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Text(
                            text = "${(model.downloadProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    LinearProgressIndicator(
                        progress = model.downloadProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                    )
                }
            }
            
            // Action Buttons
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (model.status) {
                    ModelStatus.AVAILABLE -> {
                        TextButton(onClick = onDownload) {
                            Icon(
                                Icons.Default.Download,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Download")
                        }
                    }
                    
                    ModelStatus.DOWNLOADING -> {
                        TextButton(onClick = onPause) {
                            Icon(
                                Icons.Default.Pause,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Pause")
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        TextButton(onClick = onCancel) {
                            Icon(
                                Icons.Default.Cancel,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Cancel")
                        }
                    }
                    
                    ModelStatus.PAUSED -> {
                        TextButton(onClick = onResume) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Resume")
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        TextButton(onClick = onCancel) {
                            Icon(
                                Icons.Default.Cancel,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Cancel")
                        }
                    }
                    
                    ModelStatus.DOWNLOADED -> {
                        if (!model.isPrimary) {
                            TextButton(onClick = onSetPrimary) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Set Primary")
                            }
                            
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        
                        TextButton(
                            onClick = onDelete,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete")
                        }
                    }
                    
                    ModelStatus.ERROR -> {
                        TextButton(onClick = onDownload) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Retry")
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        TextButton(
                            onClick = onDelete,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Remove")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModelInfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun ModelStatusIndicator(
    status: ModelStatus,
    downloadProgress: Float,
    modifier: Modifier = Modifier
) {
    val (color, icon) = when (status) {
        ModelStatus.AVAILABLE -> MaterialTheme.colorScheme.outline to Icons.Default.CloudDownload
        ModelStatus.DOWNLOADING -> MaterialTheme.colorScheme.primary to Icons.Default.Download
        ModelStatus.PAUSED -> Color(0xFFFF9800) to Icons.Default.Pause
        ModelStatus.DOWNLOADED -> Color(0xFF4CAF50) to Icons.Default.CheckCircle
        ModelStatus.ERROR -> MaterialTheme.colorScheme.error to Icons.Default.Error
    }
    
    Box(
        modifier = modifier
            .size(40.dp)
            .background(
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (status == ModelStatus.DOWNLOADING) {
            CircularProgressIndicator(
                progress = downloadProgress,
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp,
                color = color
            )
        } else {
            Icon(
                icon,
                contentDescription = status.name,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun EmptyModelsState(
    filter: ModelFilter,
    onAddModel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.CloudOff,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = when (filter) {
                ModelFilter.ALL -> "No models available"
                ModelFilter.DOWNLOADED -> "No models downloaded"
                ModelFilter.DOWNLOADING -> "No models downloading"
                ModelFilter.AVAILABLE -> "No models available for download"
            },
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = when (filter) {
                ModelFilter.ALL -> "Add a model to get started"
                ModelFilter.DOWNLOADED -> "Download models to use them offline"
                ModelFilter.DOWNLOADING -> "Start downloading a model"
                ModelFilter.AVAILABLE -> "Check your connection and refresh"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(onClick = onAddModel) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Model")
        }
    }
}

@Composable
private fun AddModelDialog(
    onDismiss: () -> Unit,
    onAddModel: (String, String) -> Unit
) {
    var url by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Custom Model") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Model Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("Model URL") },
                    placeholder = { Text("https://...") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onAddModel(url, name) },
                enabled = url.isNotBlank() && name.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun DeleteModelDialog(
    model: Model,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Model") },
        text = {
            Text("Are you sure you want to delete \"${model.name}\"? This action cannot be undone.")
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun formatFileSize(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    val kb = bytes / 1024.0
    if (kb < 1024) return "%.1f KB".format(kb)
    val mb = kb / 1024.0
    if (mb < 1024) return "%.1f MB".format(mb)
    val gb = mb / 1024.0
    return "%.1f GB".format(gb)
}

enum class ModelFilter(val displayName: String) {
    ALL("All"),
    DOWNLOADED("Downloaded"),
    DOWNLOADING("Downloading"),
    AVAILABLE("Available")
}