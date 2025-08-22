package com.localllm.localaichatapp.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val isLoading: Boolean = false,
    
    // AI Model Settings
    val defaultModel: String = "Gemini Nano",
    val availableModels: List<String> = listOf("Gemini Nano", "GPT-4 Mini", "Claude 3 Haiku"),
    val temperature: Float = 0.7f,
    val maxTokens: Int = 1000,
    val streamResponses: Boolean = true,
    
    // Performance Settings
    val inferenceMode: String = "Balanced",
    val useGpuAcceleration: Boolean = false,
    val memoryLimit: Int = 2048,
    val backgroundProcessing: Boolean = false,
    
    // Storage Settings
    val modelsStoragePath: String = "/data/user/0/com.localllm.localaichatapp/files/models",
    val cacheSize: Long = 156L * 1024 * 1024, // 156 MB
    val autoCleanup: String = "Weekly",
    
    // Privacy Settings
    val storeConversations: Boolean = true,
    val analytics: Boolean = false,
    val crashReports: Boolean = true,
    
    // Appearance Settings
    val theme: String = "System",
    val textSize: Float = 1.0f,
    val dynamicColors: Boolean = true,
    val compactMode: Boolean = false,
    
    // Notification Settings
    val notificationsEnabled: Boolean = true,
    val downloadNotifications: Boolean = true,
    val processingNotifications: Boolean = true,
    
    // About
    val appVersion: String = "1.0.0",
    val buildNumber: String = "100",
    
    // Dialogs
    val showDeleteAllDataDialog: Boolean = false,
    
    val error: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    // In real implementation, would inject PreferencesRepository, ModelRepository, etc.
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun loadSettings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // In real implementation, would load from SharedPreferences or DataStore
                // Simulating loading default settings
                kotlinx.coroutines.delay(500)
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to load settings: ${e.message}"
                    )
                }
            }
        }
    }

    // AI Model Settings
    fun setDefaultModel(model: String) {
        _uiState.update { it.copy(defaultModel = model) }
        saveSettings()
    }

    fun setTemperature(temperature: Float) {
        _uiState.update { it.copy(temperature = temperature) }
        saveSettings()
    }

    fun setMaxTokens(maxTokens: Int) {
        _uiState.update { it.copy(maxTokens = maxTokens) }
        saveSettings()
    }

    fun setStreamResponses(enabled: Boolean) {
        _uiState.update { it.copy(streamResponses = enabled) }
        saveSettings()
    }

    // Performance Settings
    fun setInferenceMode(mode: String) {
        _uiState.update { it.copy(inferenceMode = mode) }
        saveSettings()
    }

    fun setUseGpuAcceleration(enabled: Boolean) {
        _uiState.update { it.copy(useGpuAcceleration = enabled) }
        saveSettings()
    }

    fun setMemoryLimit(limit: Int) {
        _uiState.update { it.copy(memoryLimit = limit) }
        saveSettings()
    }

    fun setBackgroundProcessing(enabled: Boolean) {
        _uiState.update { it.copy(backgroundProcessing = enabled) }
        saveSettings()
    }

    // Storage Settings
    fun clearCache() {
        viewModelScope.launch {
            try {
                // In real implementation, would clear actual cache files
                kotlinx.coroutines.delay(1000) // Simulate cache clearing
                
                _uiState.update { 
                    it.copy(
                        cacheSize = 0L,
                        error = "Cache cleared successfully"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = "Failed to clear cache: ${e.message}")
                }
            }
        }
    }

    fun setAutoCleanup(frequency: String) {
        _uiState.update { it.copy(autoCleanup = frequency) }
        saveSettings()
    }

    // Privacy Settings
    fun setStoreConversations(enabled: Boolean) {
        _uiState.update { it.copy(storeConversations = enabled) }
        saveSettings()
    }

    fun setAnalytics(enabled: Boolean) {
        _uiState.update { it.copy(analytics = enabled) }
        saveSettings()
    }

    fun setCrashReports(enabled: Boolean) {
        _uiState.update { it.copy(crashReports = enabled) }
        saveSettings()
    }

    fun exportData() {
        viewModelScope.launch {
            try {
                // In real implementation, would export conversations and settings to file
                kotlinx.coroutines.delay(2000) // Simulate export process
                
                _uiState.update { 
                    it.copy(error = "Data exported to Downloads folder")
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = "Failed to export data: ${e.message}")
                }
            }
        }
    }

    fun showDeleteAllDataDialog() {
        _uiState.update { it.copy(showDeleteAllDataDialog = true) }
    }

    fun hideDeleteAllDataDialog() {
        _uiState.update { it.copy(showDeleteAllDataDialog = false) }
    }

    fun confirmDeleteAllData() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(showDeleteAllDataDialog = false) }
                
                // In real implementation, would delete all conversations, models, cache
                kotlinx.coroutines.delay(2000) // Simulate deletion process
                
                _uiState.update { 
                    it.copy(error = "All data deleted successfully")
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        showDeleteAllDataDialog = false,
                        error = "Failed to delete data: ${e.message}"
                    )
                }
            }
        }
    }

    // Appearance Settings
    fun setTheme(theme: String) {
        _uiState.update { it.copy(theme = theme) }
        saveSettings()
    }

    fun setTextSize(size: Float) {
        _uiState.update { it.copy(textSize = size) }
        saveSettings()
    }

    fun setDynamicColors(enabled: Boolean) {
        _uiState.update { it.copy(dynamicColors = enabled) }
        saveSettings()
    }

    fun setCompactMode(enabled: Boolean) {
        _uiState.update { it.copy(compactMode = enabled) }
        saveSettings()
    }

    // Notification Settings
    fun setNotificationsEnabled(enabled: Boolean) {
        _uiState.update { 
            it.copy(
                notificationsEnabled = enabled,
                // Disable other notification types if notifications are disabled
                downloadNotifications = if (enabled) it.downloadNotifications else false,
                processingNotifications = if (enabled) it.processingNotifications else false
            )
        }
        saveSettings()
    }

    fun setDownloadNotifications(enabled: Boolean) {
        _uiState.update { it.copy(downloadNotifications = enabled) }
        saveSettings()
    }

    fun setProcessingNotifications(enabled: Boolean) {
        _uiState.update { it.copy(processingNotifications = enabled) }
        saveSettings()
    }

    // About Actions
    fun showLicenses() {
        _uiState.update { 
            it.copy(error = "Would open licenses screen")
        }
    }

    fun contactSupport() {
        _uiState.update { 
            it.copy(error = "Would open email client or support chat")
        }
    }

    // Reset
    fun resetToDefaults() {
        viewModelScope.launch {
            try {
                _uiState.update { 
                    SettingsUiState().copy(
                        availableModels = it.availableModels,
                        appVersion = it.appVersion,
                        buildNumber = it.buildNumber,
                        modelsStoragePath = it.modelsStoragePath
                    )
                }
                
                saveSettings()
                
                _uiState.update { 
                    it.copy(error = "Settings reset to defaults")
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = "Failed to reset settings: ${e.message}")
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun saveSettings() {
        viewModelScope.launch {
            try {
                // In real implementation, would save to SharedPreferences or DataStore
                // For now, just simulate saving
                kotlinx.coroutines.delay(100)
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = "Failed to save settings: ${e.message}")
                }
            }
        }
    }
}