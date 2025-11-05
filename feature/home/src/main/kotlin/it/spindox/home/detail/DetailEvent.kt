package it.spindox.home.detail

data class DetailEvent (
    val onBackButtonClick: () -> Unit,
    val onRetry: () -> Unit,
)

val emptyDetailEvent = DetailEvent (
    onBackButtonClick = {},
    onRetry = {},
)