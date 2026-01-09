package it.spindox.data.model

sealed class FunctionCallEvent {
    data class Error(val message: String) : FunctionCallEvent()
    data class IncreaseVolume(val level: Int) : FunctionCallEvent()
    object SwitchTheme : FunctionCallEvent()
}
