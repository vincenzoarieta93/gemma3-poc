package it.spindox.home.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import it.spindox.designsystem.utils.ThemePreviews
import it.spindox.result.Resource
import it.spindox.result.loading
import it.spindox.designsystem.theme.MainAppTheme

@Composable
fun MainPage(
    viewModel: MainViewModel = hiltViewModel(),
    onModelSelected: () -> Unit,
) {

    val state = viewModel.uiState.collectAsState().value
    val event = viewModel.event.copy(
        onModelSelected = { model ->
            viewModel.onLlmModelUiSelected(model)
            onModelSelected()
        }
    )

    MainPageUi(
        state = state,
        event = event,
    )
}

@Composable
fun MainPageUi(
    state: MainState,
    event: MainEvent,
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (state.modelsList) {
            is Resource.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(state.modelsList.data) { index, item ->
                        ItemCard(
                            model = item,
                            onClick = {
                                event.onModelSelected(item)
                            },
                        )
                    }
                }
            }

            is Resource.Error -> {
                Text(
                    text = "Error: ${state.modelsList.getErrorMessage()}"
                )
            }

            Resource.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun ItemCard(
    model: LlmModelUi,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ── Title Row ──────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = model.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
            }

            // ── Model info row ─────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ModelInfo("Temp", model.temperature.toString())
                ModelInfo("Top-K", model.topK.toString())
                ModelInfo("Top-P", model.topP.toString())
            }

            // ── Footer row ─────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (model.needsAuth) "🔒 Requires login" else "No login required",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = model.preferredBackend?.name.orEmpty(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ModelBackendChip(backend: LlmInference.Backend) {
    val color = when (backend) {
        LlmInference.Backend.CPU -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.tertiary
    }
    backend.let {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(color.copy(alpha = 0.15f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = it.name,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        }
    }
}

@Composable
private fun ModelInfo(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


// -- Previews -- //

@ThemePreviews
@Composable
private fun MainPageUiResultsPreview() = MainAppTheme {
    MainPageUi(
        state = sampleMainState,
        event = emptyMainEvent,
    )
}

@ThemePreviews
@Composable
private fun MainPageUiLoadingPreview() = MainAppTheme {
    MainPageUi(
        state = sampleMainState.copy(
            modelsList = loading()
        ),
        event = emptyMainEvent,
    )
}