package com.aiuniverse.app.presentation.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash_screen")
    data object Onboarding : Screen("onboarding_screen")
    data object Home : Screen("home_screen")
    data object Chat : Screen("chat_screen?sessionId={sessionId}") {
        fun createRoute(sessionId: String? = null): String =
            if (sessionId != null) "chat_screen?sessionId=$sessionId" else "chat_screen"
    }
    data object History : Screen("history_screen")
    data object ModelSelection : Screen("model_selection_screen")
    data object ModelComparison : Screen("model_comparison_screen")
    data object ImageGeneration : Screen("image_generation_screen")
    data object AiTools : Screen("ai_tools_screen")
    data object Settings : Screen("settings_screen")
    data object Profile : Screen("profile_screen")
    data object ApiManagement : Screen("api_management_screen")
    data object CommandCenter : Screen("command_center_screen")
}
