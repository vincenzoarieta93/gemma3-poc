package it.spindox.gemma3.ui.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import it.spindox.gemma3.MainActivityViewModel
import it.spindox.home.main.MainRoute
import it.spindox.home.preparation.PreparationRoute
import it.spindox.home.speech.SpeechRoute
import it.spindox.navigation.AppRoute
import it.spindox.navigation.MainNavigationViewModel

@Composable
fun MainNavigation(
    snackbarHostState: SnackbarHostState,
    startDestination: String?,
    onGoToLogin: () -> Unit
) {
    val navController = rememberNavController()
    val sharedViewModel: MainNavigationViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination ?: AppRoute.ModelSelectionScreen.route
    ) {
        composable(AppRoute.ModelSelectionScreen.route) {
            MainRoute(
                sharedViewModel = sharedViewModel,
                onModelSelected = {
                    navController.navigate(AppRoute.PreparationScreen.route) {
                        popUpTo(AppRoute.ModelSelectionScreen.route) { inclusive = false }
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
                        popUpTo(AppRoute.PreparationScreen.route) { inclusive = true }
                        launchSingleTop = true
                    }
                    sharedViewModel.triggerModelDownloadedEvent()
                },
                onDownloadCancelled = {
                    navController.navigate(AppRoute.ModelSelectionScreen.route) {
                        popUpTo(AppRoute.ModelSelectionScreen.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onGoToLogin = {
                    onGoToLogin()
                },
                onGoBack = {
                    navController.navigate(AppRoute.ModelSelectionScreen.route) {
                        popUpTo(AppRoute.ModelSelectionScreen.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(AppRoute.SpeechScreen.route) {
            SpeechRoute(
                snackbarHostState = snackbarHostState,
                onNavigateToDestination = { destinationRoute ->
                    when (destinationRoute) {
                        AppRoute.ModelSelectionScreen ->
                            navController.navigate(AppRoute.ModelSelectionScreen.route) {
                                popUpTo(AppRoute.ModelSelectionScreen.route) { inclusive = false }
                                launchSingleTop = true
                            }

                        else -> {
                            // Do nothing
                        }
                    }
                }
            )
        }
    }
}