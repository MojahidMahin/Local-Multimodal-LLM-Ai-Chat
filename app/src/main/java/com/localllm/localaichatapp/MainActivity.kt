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
import androidx.navigation.compose.rememberNavController
import com.localllm.localaichatapp.presentation.theme.LocalAiChatAppTheme
import com.localllm.localaichatapp.presentation.navigation.LocalAiChatAppNavigation
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
    
    LocalAiChatAppNavigation(
        navController = navController,
        modifier = Modifier.fillMaxSize()
    )
}