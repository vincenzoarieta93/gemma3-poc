package it.spindox.gemma3.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import it.spindox.home.main.MainPage

const val MODEL_SELECTION_SCREEN = "model_selection_screen"
const val LOAD_SCREEN = "load_screen"
const val CHAT_SCREEN = "chat_screen"

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = MODEL_SELECTION_SCREEN
    ) {
        composable(MODEL_SELECTION_SCREEN) {
            MainPage(
                onModelSelected = {
                    navController.navigate(LOAD_SCREEN) {
                        popUpTo(MODEL_SELECTION_SCREEN) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(LOAD_SCREEN) { backStackEntry ->
            TODO()
        }
    }
}