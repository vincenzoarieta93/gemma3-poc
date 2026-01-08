package it.spindox.gemma3.ui.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import it.spindox.home.main.MainPage
import it.spindox.home.preparation.PreparationRoute
import it.spindox.home.speech.SpeechRoute
import it.spindox.navigation.AppRoute

@Composable
fun MainNavigation(
    snackbarHostState: SnackbarHostState,
    startDestination: String?,
    onGoToLogin: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination ?: AppRoute.ModelSelectionScreen.route
    ) {
        composable(AppRoute.ModelSelectionScreen.route) {
            MainPage(
                onModelSelected = {
                    navController.navigate(AppRoute.PreparationScreen.route) {
                        popUpTo(AppRoute.PreparationScreen.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(AppRoute.PreparationScreen.route) { _ ->
            PreparationRoute(
                snackbarHostState = snackbarHostState,
                onModelLoaded = {
                    navController.navigate(AppRoute.SpeechScreen.route) {
                        popUpTo(AppRoute.SpeechScreen.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onDownloadCancelled = {
                    navController.navigate(AppRoute.ModelSelectionScreen.route) {
                        popUpTo(AppRoute.ModelSelectionScreen.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onGoToLogin = {
                    onGoToLogin()
                },
                onGoBack = {
                    navController.navigate(AppRoute.ModelSelectionScreen.route) {
                        popUpTo(AppRoute.PreparationScreen.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(AppRoute.SpeechScreen.route) {
            SpeechRoute()
        }
    }
}