package it.spindox.home.main

data class MainEvent (
    val onModelSelected: (LlmModelUi) -> Unit,
)

val emptyMainEvent = MainEvent { _ -> }