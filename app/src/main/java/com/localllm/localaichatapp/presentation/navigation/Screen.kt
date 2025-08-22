package com.localllm.localaichatapp.presentation.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object ModelManager : Screen("model_manager")
    
    // Task screens with parameters
    object Chat : Screen("chat/{modelId}") {
        fun createRoute(modelId: String) = "chat/$modelId"
    }
    
    object AskImage : Screen("ask_image/{modelId}") {
        fun createRoute(modelId: String) = "ask_image/$modelId"
    }
    
    object AskAudio : Screen("ask_audio/{modelId}") {
        fun createRoute(modelId: String) = "ask_audio/$modelId"
    }
    
    object PromptLab : Screen("prompt_lab/{modelId}") {
        fun createRoute(modelId: String) = "prompt_lab/$modelId"
    }
    
    // Settings and info screens
    object Settings : Screen("settings")
    object About : Screen("about")
    object Performance : Screen("performance/{modelId}") {
        fun createRoute(modelId: String) = "performance/$modelId"
    }
    
    // Session-specific screens
    object ChatSession : Screen("chat_session/{sessionId}") {
        fun createRoute(sessionId: String) = "chat_session/$sessionId"
    }
    
    object ImageSession : Screen("image_session/{sessionId}") {
        fun createRoute(sessionId: String) = "image_session/$sessionId"
    }
    
    object AudioSession : Screen("audio_session/{sessionId}") {
        fun createRoute(sessionId: String) = "audio_session/$sessionId"
    }
}

// Navigation arguments
object NavArgs {
    const val MODEL_ID = "modelId"
    const val SESSION_ID = "sessionId"
}