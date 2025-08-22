package com.localllm.localaichatapp.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        viewModel.loadSettings()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.resetToDefaults() }) {
                        Icon(Icons.Default.RestartAlt, contentDescription = "Reset to defaults")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // AI Model Settings
            item {
                SettingsSection(
                    title = "AI Model Settings",
                    icon = Icons.Default.Psychology
                ) {
                    SettingsDropdown(
                        title = "Default Model",
                        subtitle = "Primary model for AI tasks",
                        currentValue = uiState.defaultModel,
                        options = uiState.availableModels,
                        onValueChanged = viewModel::setDefaultModel
                    )
                    
                    SettingsSlider(
                        title = "Response Temperature",
                        subtitle = "Controls creativity (0.0 = focused, 1.0 = creative)",
                        value = uiState.temperature,
                        range = 0f..1f,
                        onValueChanged = viewModel::setTemperature
                    )
                    
                    SettingsSlider(
                        title = "Max Response Length",
                        subtitle = "Maximum tokens in AI responses",
                        value = uiState.maxTokens.toFloat(),
                        range = 100f..4000f,
                        steps = 39,
                        onValueChanged = { viewModel.setMaxTokens(it.toInt()) }
                    )
                    
                    SettingsToggle(
                        title = "Stream Responses",
                        subtitle = "Show responses as they're generated",
                        checked = uiState.streamResponses,
                        onCheckedChange = viewModel::setStreamResponses
                    )
                }
            }
            
            // Performance Settings
            item {
                SettingsSection(
                    title = "Performance",
                    icon = Icons.Default.Speed
                ) {
                    SettingsDropdown(
                        title = "Inference Mode",
                        subtitle = "Balance between speed and quality",
                        currentValue = uiState.inferenceMode,
                        options = listOf("Fast", "Balanced", "Quality"),
                        onValueChanged = viewModel::setInferenceMode
                    )
                    
                    SettingsToggle(
                        title = "GPU Acceleration",
                        subtitle = "Use GPU for faster inference (if available)",
                        checked = uiState.useGpuAcceleration,
                        onCheckedChange = viewModel::setUseGpuAcceleration
                    )
                    
                    SettingsSlider(
                        title = "Memory Usage Limit",
                        subtitle = "Maximum memory for AI models (MB)",
                        value = uiState.memoryLimit.toFloat(),
                        range = 512f..8192f,
                        steps = 15,
                        onValueChanged = { viewModel.setMemoryLimit(it.toInt()) }
                    )
                    
                    SettingsToggle(
                        title = "Background Processing",
                        subtitle = "Continue processing when app is minimized",
                        checked = uiState.backgroundProcessing,
                        onCheckedChange = viewModel::setBackgroundProcessing
                    )
                }
            }
            
            // Storage Settings
            item {
                SettingsSection(
                    title = "Storage",
                    icon = Icons.Default.Storage
                ) {
                    SettingsInfo(
                        title = "Models Storage",
                        subtitle = "Location where AI models are stored",
                        value = uiState.modelsStoragePath
                    )
                    
                    SettingsInfo(
                        title = "Cache Size",
                        subtitle = "Current cache usage",
                        value = formatFileSize(uiState.cacheSize)
                    )
                    
                    SettingsButton(
                        title = "Clear Cache",
                        subtitle = "Free up space by clearing temporary files",
                        icon = Icons.Default.CleaningServices,
                        onClick = { viewModel.clearCache() }
                    )
                    
                    SettingsDropdown(
                        title = "Auto-cleanup",
                        subtitle = "Automatically clean old files",
                        currentValue = uiState.autoCleanup,
                        options = listOf("Never", "Weekly", "Monthly"),
                        onValueChanged = viewModel::setAutoCleanup
                    )
                }
            }
            
            // Privacy Settings
            item {
                SettingsSection(
                    title = "Privacy",
                    icon = Icons.Default.PrivacyTip
                ) {
                    SettingsToggle(
                        title = "Store Conversations",
                        subtitle = "Save chat history locally",
                        checked = uiState.storeConversations,
                        onCheckedChange = viewModel::setStoreConversations
                    )
                    
                    SettingsToggle(
                        title = "Analytics",
                        subtitle = "Help improve the app with usage data",
                        checked = uiState.analytics,
                        onCheckedChange = viewModel::setAnalytics
                    )
                    
                    SettingsToggle(
                        title = "Crash Reports",
                        subtitle = "Automatically send crash reports",
                        checked = uiState.crashReports,
                        onCheckedChange = viewModel::setCrashReports
                    )
                    
                    SettingsButton(
                        title = "Export Data",
                        subtitle = "Export your conversations and settings",
                        icon = Icons.Default.FileDownload,
                        onClick = { viewModel.exportData() }
                    )
                    
                    SettingsButton(
                        title = "Delete All Data",
                        subtitle = "Permanently remove all conversations and models",
                        icon = Icons.Default.DeleteForever,
                        iconTint = MaterialTheme.colorScheme.error,
                        onClick = { viewModel.showDeleteAllDataDialog() }
                    )
                }
            }
            
            // Theme & Appearance
            item {
                SettingsSection(
                    title = "Appearance",
                    icon = Icons.Default.Palette
                ) {
                    SettingsDropdown(
                        title = "Theme",
                        subtitle = "App color theme",
                        currentValue = uiState.theme,
                        options = listOf("System", "Light", "Dark"),
                        onValueChanged = viewModel::setTheme
                    )
                    
                    SettingsSlider(
                        title = "Text Size",
                        subtitle = "Adjust text size throughout the app",
                        value = uiState.textSize,
                        range = 0.8f..1.4f,
                        onValueChanged = viewModel::setTextSize
                    )
                    
                    SettingsToggle(
                        title = "Dynamic Colors",
                        subtitle = "Use system accent colors (Android 12+)",
                        checked = uiState.dynamicColors,
                        onCheckedChange = viewModel::setDynamicColors
                    )
                    
                    SettingsToggle(
                        title = "Compact Mode",
                        subtitle = "Show more content on screen",
                        checked = uiState.compactMode,
                        onCheckedChange = viewModel::setCompactMode
                    )
                }
            }
            
            // Notifications
            item {
                SettingsSection(
                    title = "Notifications",
                    icon = Icons.Default.Notifications
                ) {
                    SettingsToggle(
                        title = "Enable Notifications",
                        subtitle = "Receive app notifications",
                        checked = uiState.notificationsEnabled,
                        onCheckedChange = viewModel::setNotificationsEnabled
                    )
                    
                    SettingsToggle(
                        title = "Download Complete",
                        subtitle = "Notify when model downloads finish",
                        checked = uiState.downloadNotifications,
                        enabled = uiState.notificationsEnabled,
                        onCheckedChange = viewModel::setDownloadNotifications
                    )
                    
                    SettingsToggle(
                        title = "Processing Complete",
                        subtitle = "Notify when long AI tasks finish",
                        checked = uiState.processingNotifications,
                        enabled = uiState.notificationsEnabled,
                        onCheckedChange = viewModel::setProcessingNotifications
                    )
                }
            }
            
            // About
            item {
                SettingsSection(
                    title = "About",
                    icon = Icons.Default.Info
                ) {
                    SettingsInfo(
                        title = "Version",
                        subtitle = "App version",
                        value = uiState.appVersion
                    )
                    
                    SettingsInfo(
                        title = "Build",
                        subtitle = "Build number",
                        value = uiState.buildNumber
                    )
                    
                    SettingsButton(
                        title = "Licenses",
                        subtitle = "Open source licenses",
                        icon = Icons.Default.Description,
                        onClick = { viewModel.showLicenses() }
                    )
                    
                    SettingsButton(
                        title = "Contact Support",
                        subtitle = "Get help or report issues",
                        icon = Icons.Default.ContactSupport,
                        onClick = { viewModel.contactSupport() }
                    )
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
    
    // Delete All Data Dialog
    if (uiState.showDeleteAllDataDialog) {
        DeleteAllDataDialog(
            onConfirm = { viewModel.confirmDeleteAllData() },
            onDismiss = { viewModel.hideDeleteAllDataDialog() }
        )
    }
    
    // Error handling
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Show error message
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            content()
        }
    }
}

@Composable
private fun SettingsToggle(
    title: String,
    subtitle: String,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = enabled) { onCheckedChange(!checked) }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (enabled) MaterialTheme.colorScheme.onSurface 
                       else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (enabled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                       else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}

@Composable
private fun SettingsSlider(
    title: String,
    subtitle: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    onValueChanged: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            Text(
                text = if (steps > 0) value.toInt().toString() else "%.2f".format(value),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Slider(
            value = value,
            onValueChange = onValueChanged,
            valueRange = range,
            steps = steps
        )
    }
}

@Composable
private fun SettingsDropdown(
    title: String,
    subtitle: String,
    currentValue: String,
    options: List<String>,
    onValueChanged: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(vertical = 4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentValue,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChanged(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun SettingsInfo(
    title: String,
    subtitle: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun SettingsButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
    }
}

@Composable
private fun DeleteAllDataDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete All Data") },
        text = {
            Text("This will permanently delete all conversations, models, and settings. This action cannot be undone. Are you sure you want to continue?")
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete All")
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