package it.spindox.data.model

sealed class FunctionCallEvent {
    data class Error(val message: String) : FunctionCallEvent()
    data class NavigateToDestination(val destination: String) : FunctionCallEvent()
    object SwitchTheme : FunctionCallEvent()
}
