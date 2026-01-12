package it.spindox.data.model

sealed class LlmResponse {
    data class Text(val text: String) : LlmResponse()
    object SwitchThemeCall : LlmResponse()
    data class NavigateToDestination(val destination: String) : LlmResponse()
    object OpenWiFiSettingsScreen : LlmResponse()
    data class UnknownFunctionCall(val message: String) : LlmResponse()
}