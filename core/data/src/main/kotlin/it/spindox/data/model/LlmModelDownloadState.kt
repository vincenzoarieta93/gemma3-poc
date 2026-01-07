package it.spindox.data.model

import java.io.File

sealed interface LlmModelDownloadState {
    data class DownloadInProgress(val percentage: Int) : LlmModelDownloadState
    data class DownloadFailure(val reason: Throwable) : LlmModelDownloadState
    data class DownloadComplete(val file: File) : LlmModelDownloadState
}