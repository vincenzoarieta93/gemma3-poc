package it.spindox.home.preparation

data class PreparationEvents(
    val onModelLoaded: () -> Unit = { },
    val onGoBack: () -> Unit = {},
    val onGoToLogin: () -> Unit = {},
    val onCancelDownload: () -> Unit = {},
    val onDownloadCancelled: () -> Unit = {}
)