package com.localllm.localaichatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.localllm.localaichatapp.presentation.chat.ChatScreen
import com.localllm.localaichatapp.presentation.models.ModelManagementScreen
import com.localllm.localaichatapp.ui.theme.LocalAiChatAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LocalAiChatAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AiChatApp()
                }
            }
        }
    }
}

@Composable
fun AiChatApp() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "chat"
    ) {
        composable("chat") {
            ChatScreen(
                onNavigateToModels = {
                    navController.navigate("models")
                }
            )
        }
        
        composable("models") {
            ModelManagementScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}