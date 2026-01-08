package it.spindox.navigation

sealed class AppRoute(val route: String) {
    data object ModelSelectionScreen : AppRoute("model_selection_screen")
    data object PreparationScreen : AppRoute("preparation_screen")
    data object SpeechScreen : AppRoute("speech_screen")
}