package it.spindox.home.preparation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.spindox.coroutine.DefaultDispatcherProvider
import it.spindox.data.exceptions.MissingAccessTokenException
import it.spindox.data.exceptions.MissingUrlException
import it.spindox.data.exceptions.ModelNotFoundException
import it.spindox.data.exceptions.UnauthorizedAccessException
import it.spindox.data.model.LlmModelDownloadState
import it.spindox.domain.usecase.CheckModelFileUseCase
import it.spindox.domain.usecase.DeleteModelFileUseCase
import it.spindox.domain.usecase.DownloadLlmModelUseCase
import it.spindox.domain.usecase.GetSelectedModelUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreparationViewModel @Inject constructor(
    private val dispatcherProvider: DefaultDispatcherProvider,
    private val getSelectedModelUseCase: GetSelectedModelUseCase,
    private val checkModelFileUseCase: CheckModelFileUseCase,
    private val deleteModelFileUseCase: DeleteModelFileUseCase,
    private val downloadLlmModelUseCase: DownloadLlmModelUseCase
) : ViewModel() {

    private val emptyState = PreparationState(0, false, null)

    private val _stateUi: MutableStateFlow<PreparationState> = MutableStateFlow(emptyState)
    val stateUi: StateFlow<PreparationState> = _stateUi

    private val _downloadEvent = MutableSharedFlow<DownloadEvent>()
    val downloadEvent: SharedFlow<DownloadEvent> = _downloadEvent

    private var downloadJob: Job? = null

    fun cancelDownload() {
        viewModelScope.launch {
            downloadJob?.cancel()
            deleteModelFileUseCase()
            _stateUi.update { oldState ->
                oldState.copy(
                    isDownloading = false,
                    errorMessage = null,
                    progress = 0
                )
            }

        }
    }

    fun prepareLlmModel() {
        downloadJob = viewModelScope.launch(dispatcherProvider.io) {
            if (!checkModelFileUseCase()) {
                // Model not stored locally
                val selectedModel = getSelectedModelUseCase()
                if (selectedModel == null) {
                    onDownloadFailed(ModelNotFoundException())
                    return@launch
                }

                if (selectedModel.modelPath.isBlank()) {
                    onDownloadFailed(MissingUrlException())
                    return@launch
                } else {
                    downloadLlmModelUseCase(selectedModel.model, selectedModel.modelPath).collect { downloadState ->
                        when (downloadState) {
                            is LlmModelDownloadState.DownloadInProgress -> {
                                _stateUi.update { oldState ->
                                    oldState.copy(
                                        isDownloading = true,
                                        errorMessage = null,
                                        progress = downloadState.percentage
                                    )
                                }
                            }
                            is LlmModelDownloadState.DownloadComplete -> {
                                _stateUi.update { oldState ->
                                    oldState.copy(
                                        isDownloading = false,
                                        errorMessage = null,
                                        progress = 100
                                    )
                                }
                                _downloadEvent.emit(DownloadEvent.DownloadCompleted)
                            }
                            is LlmModelDownloadState.DownloadFailure -> {
                                onDownloadFailed(downloadState.reason)
                            }
                        }
                    }
                }
            } else {
                _stateUi.update { oldState ->
                    oldState.copy(
                        isDownloading = false,
                        errorMessage = null,
                        progress = 100
                    )
                }
                _downloadEvent.emit(DownloadEvent.DownloadCompleted)
            }
        }
    }

    private suspend fun onDownloadFailed(reason: Throwable) {
        when (reason) {
            is MissingAccessTokenException -> {
                _downloadEvent.emit(DownloadEvent.MissingAccessToken)
                // handleDownloadFailure(reason.message.orEmpty())
            }

            is UnauthorizedAccessException -> {
                handleDownloadFailure(reason.message.orEmpty())
            }

            else -> {
                handleDownloadFailure(reason.localizedMessage.orEmpty())
            }
        }
    }

    private suspend fun handleDownloadFailure(errorMessage: String) {
        _downloadEvent.emit(DownloadEvent.DownloadFailed(errorMessage))
        _stateUi.update { oldState ->
            oldState.copy(
                isDownloading = false,
                errorMessage = errorMessage
            )
        }
    }
}

data class PreparationState(
    val progress: Int,
    val isDownloading: Boolean,
    val errorMessage: String?
)

sealed interface DownloadEvent {
    object DownloadCompleted : DownloadEvent
    object MissingAccessToken : DownloadEvent
    data class DownloadFailed(val errorMessage: String) : DownloadEvent
}
