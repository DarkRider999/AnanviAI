package com.aiuniverse.app.presentation.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aiuniverse.app.presentation.apimanagement.ApiManagementScreen
import com.aiuniverse.app.presentation.chat.ChatScreen
import com.aiuniverse.app.presentation.commandcenter.CommandCenterScreen
import com.aiuniverse.app.presentation.compare.ModelComparisonScreen
import com.aiuniverse.app.presentation.history.HistoryScreen
import com.aiuniverse.app.presentation.home.HomeScreen
import com.aiuniverse.app.presentation.imagegen.ImageGenerationScreen
import com.aiuniverse.app.presentation.models.ModelSelectionScreen
import com.aiuniverse.app.presentation.onboarding.OnboardingScreen
import com.aiuniverse.app.presentation.profile.ProfileScreen
import com.aiuniverse.app.presentation.settings.SettingsScreen
import com.aiuniverse.app.presentation.splash.SplashScreen
import com.aiuniverse.app.presentation.tools.AiToolsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToChat = { sessionId ->
                    navController.navigate(Screen.Chat.createRoute(sessionId))
                },
                onNavigateToCompare = { navController.navigate(Screen.ModelComparison.route) },
                onNavigateToTools = { navController.navigate(Screen.AiTools.route) },
                onNavigateToImageGen = { navController.navigate(Screen.ImageGeneration.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToApiManagement = { navController.navigate(Screen.ApiManagement.route) },
                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                onNavigateToCommandCenter = { navController.navigate(Screen.CommandCenter.route) }
            )
        }

        composable(
            route = Screen.Chat.route,
            arguments = listOf(navArgument("sessionId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) {
            ChatScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                onNavigateBack = { navController.popBackStack() },
                onSessionSelected = { sessionId ->
                    navController.navigate(Screen.Chat.createRoute(sessionId))
                }
            )
        }

        composable(Screen.ModelSelection.route) {
            ModelSelectionScreen(
                onNavigateBack = { navController.popBackStack() },
                onModelSelected = { model ->
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.ModelComparison.route) {
            ModelComparisonScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ImageGeneration.route) {
            ImageGenerationScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AiTools.route) {
            AiToolsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToApiKeys = { navController.navigate(Screen.ApiManagement.route) }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ApiManagement.route) {
            ApiManagementScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.CommandCenter.route) {
            CommandCenterScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
