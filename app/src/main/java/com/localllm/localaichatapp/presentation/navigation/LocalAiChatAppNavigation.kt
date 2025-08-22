package com.localllm.localaichatapp.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.localllm.localaichatapp.presentation.home.HomeScreen
import com.localllm.localaichatapp.presentation.chat.ChatScreen
import com.localllm.localaichatapp.presentation.askimage.AskImageScreen
import com.localllm.localaichatapp.presentation.askaudio.AskAudioScreen
import com.localllm.localaichatapp.presentation.promptlab.PromptLabScreen
import com.localllm.localaichatapp.presentation.modelmanager.ModelManagerScreen
import com.localllm.localaichatapp.presentation.settings.SettingsScreen
import com.localllm.localaichatapp.presentation.performance.PerformanceScreen

@Composable
fun LocalAiChatAppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300, easing = EaseInOut)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300, easing = EaseInOut)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300, easing = EaseInOut)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300, easing = EaseInOut)
            )
        }
    ) {
        // Home screen - main task selection
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToTask = { taskType, modelId ->
                    when (taskType) {
                        "CHAT" -> navController.navigate(Screen.Chat.createRoute(modelId))
                        "ASK_IMAGE" -> navController.navigate(Screen.AskImage.createRoute(modelId))
                        "ASK_AUDIO" -> navController.navigate(Screen.AskAudio.createRoute(modelId))
                        "PROMPT_LAB" -> navController.navigate(Screen.PromptLab.createRoute(modelId))
                    }
                },
                onNavigateToModelManager = {
                    navController.navigate(Screen.ModelManager.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        
        // Model Manager screen
        composable(Screen.ModelManager.route) {
            ModelManagerScreen(
                onBackClick = { navController.navigateUp() },
                onModelSelected = { modelId, taskType ->
                    when (taskType) {
                        "CHAT" -> navController.navigate(Screen.Chat.createRoute(modelId))
                        "ASK_IMAGE" -> navController.navigate(Screen.AskImage.createRoute(modelId))
                        "ASK_AUDIO" -> navController.navigate(Screen.AskAudio.createRoute(modelId))
                        "PROMPT_LAB" -> navController.navigate(Screen.PromptLab.createRoute(modelId))
                    }
                }
            )
        }
        
        // Chat screen
        composable(
            route = Screen.Chat.route,
            arguments = listOf(navArgument(NavArgs.MODEL_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val modelId = backStackEntry.arguments?.getString(NavArgs.MODEL_ID) ?: return@composable
            ChatScreen(
                modelId = modelId,
                onBackClick = { navController.navigateUp() },
                onNavigateToPerformance = { 
                    navController.navigate(Screen.Performance.createRoute(modelId))
                }
            )
        }
        
        // Ask Image screen
        composable(
            route = Screen.AskImage.route,
            arguments = listOf(navArgument(NavArgs.MODEL_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val modelId = backStackEntry.arguments?.getString(NavArgs.MODEL_ID) ?: return@composable
            AskImageScreen(
                modelId = modelId,
                onBackClick = { navController.navigateUp() },
                onNavigateToPerformance = { 
                    navController.navigate(Screen.Performance.createRoute(modelId))
                }
            )
        }
        
        // Ask Audio screen
        composable(
            route = Screen.AskAudio.route,
            arguments = listOf(navArgument(NavArgs.MODEL_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val modelId = backStackEntry.arguments?.getString(NavArgs.MODEL_ID) ?: return@composable
            AskAudioScreen(
                modelId = modelId,
                onBackClick = { navController.navigateUp() },
                onNavigateToPerformance = { 
                    navController.navigate(Screen.Performance.createRoute(modelId))
                }
            )
        }
        
        // Prompt Lab screen
        composable(
            route = Screen.PromptLab.route,
            arguments = listOf(navArgument(NavArgs.MODEL_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val modelId = backStackEntry.arguments?.getString(NavArgs.MODEL_ID) ?: return@composable
            PromptLabScreen(
                modelId = modelId,
                onBackClick = { navController.navigateUp() },
                onNavigateToPerformance = { 
                    navController.navigate(Screen.Performance.createRoute(modelId))
                }
            )
        }
        
        // Performance screen
        composable(
            route = Screen.Performance.route,
            arguments = listOf(navArgument(NavArgs.MODEL_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val modelId = backStackEntry.arguments?.getString(NavArgs.MODEL_ID) ?: return@composable
            PerformanceScreen(
                modelId = modelId,
                onBackClick = { navController.navigateUp() }
            )
        }
        
        // Settings screen
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = { navController.navigateUp() }
            )
        }
    }
}