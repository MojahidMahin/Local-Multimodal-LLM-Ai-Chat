package com.localllm.localaichatapp

import android.app.Application
import com.localllm.localaichatapp.data.service.PromptTemplateInitializer
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class AiChatApplication : Application() {
    
    @Inject
    lateinit var promptTemplateInitializer: PromptTemplateInitializer
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize built-in prompt templates
        applicationScope.launch {
            try {
                promptTemplateInitializer.initializeDefaultTemplates()
            } catch (e: Exception) {
                // Log error but don't crash the app
                android.util.Log.e("LocalAiChatApp", "Failed to initialize prompt templates", e)
            }
        }
    }
}