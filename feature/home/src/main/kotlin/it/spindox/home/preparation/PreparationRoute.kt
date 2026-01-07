package it.spindox.home.preparation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.spindox.home.R

@Composable
fun PreparationRoute(
    viewModel: PreparationViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
    onModelLoaded: () -> Unit = { },
    onDownloadCancelled: () -> Unit = { },
    onGoToLogin: () -> Unit = {},
    onGoBack: () -> Unit = {},
) {
    val state = viewModel.stateUi.collectAsState().value
    val preparationEvents = PreparationEvents(
        onModelLoaded = onModelLoaded,
        onGoBack = onGoBack,
        onGoToLogin = onGoToLogin,
        onCancelDownload = {
            viewModel.cancelDownload()
            onDownloadCancelled()
        },
    )

    LaunchedEffect(Unit) {
        viewModel.prepareLlmModel()
    }

    LaunchedEffect(Unit) {
        viewModel.downloadEvent.collect { event ->
            when (event) {
                is DownloadEvent.DownloadCompleted -> {
                    onModelLoaded()
                }

                is DownloadEvent.DownloadFailed -> {
                    snackbarHostState.showSnackbar(
                        message = event.errorMessage, withDismissAction = true
                    )
                }

                is DownloadEvent.MissingAccessToken -> {
                    preparationEvents.onGoToLogin()
                }

                is DownloadEvent.DownloadCancelled -> {
                    snackbarHostState.showSnackbar(
                        message = "Download cancelled", withDismissAction = true
                    )
                }
            }
        }
    }

    PreparationScreenUi(state, preparationEvents)
}

@Composable
fun PreparationScreenUi(state: PreparationState, events: PreparationEvents) {
    val errorMessage = state.errorMessage
    val progress = state.progress
    val isDownloading = state.isDownloading

    when {
        !errorMessage.isNullOrBlank() -> {
            ErrorMessage(errorMessage, events.onGoBack)
        }

        isDownloading -> {
            DownloadIndicator(progress) {
                events.onCancelDownload()
            }
        }

        else -> {
            LoadingIndicator()
        }
    }
}


@Composable
fun DownloadIndicator(progress: Int, onCancel: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Downloading Model: $progress%",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        CircularProgressIndicator(progress = { progress / 100f })
        Button(onClick = onCancel, modifier = Modifier.padding(top = 8.dp)) {
            Text("Cancel")
        }
    }
}

@Composable
fun LoadingIndicator() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.loading_model),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorMessage(
    errorMessage: String, onGoBack: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
        Button(onClick = onGoBack, modifier = Modifier.padding(top = 16.dp)) {
            Text("Go Back")
        }
    }
}

@Preview
@Composable
fun PreviewErrorMessage() {
    ErrorMessage("Error message", {})
}

@Preview
@Composable
fun PreviewLoadingIndicator() {
    LoadingIndicator()
}

@Preview
@Composable
fun PreviewDownloadIndicator() {
    DownloadIndicator(50) {}
}

