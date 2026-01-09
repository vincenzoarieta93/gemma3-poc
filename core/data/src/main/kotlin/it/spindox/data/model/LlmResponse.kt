package it.spindox.data.model

sealed class LlmResponse {
    data class Text(val text: String) : LlmResponse()
    data class SwitchThemeCall(val name: String) : LlmResponse()
    data class IncreaseDeviceVolumeCall(val name: String, val level: Int) : LlmResponse()
    data class UnknownFunctionCall(val message: String) : LlmResponse()
}