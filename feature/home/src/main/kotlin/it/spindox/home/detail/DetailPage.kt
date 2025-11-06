package it.spindox.home.detail

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.spindox.designsystem.utils.ThemePreviews
import it.spindox.designsystem.theme.MainAppTheme
import kotlin.collections.listOf

@Composable
fun DetailPage(
    viewModel: DetailViewModel = hiltViewModel(),
    name: String,
    detailsUrl: String,
    onGoBack: () -> Unit,
) {
    LaunchedEffect(Unit) {
        viewModel.onPokemonSelected(PokemonReference(name, detailsUrl))
    }

    val state = viewModel.uiState.collectAsState().value
    val event = DetailEvent(
        onBackButtonClick = { onGoBack() },
        onRetry = {
            // TODO("implement this method")
        }
    )

    DetailPageUi(
        state = state,
        event = event,
    )
}

@Composable
fun DetailPageUi(
    state: DetailScreenState,
    event: DetailEvent,
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(48.dp)
                .clickable {
                    event.onBackButtonClick()
                }
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Back",
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(16.dp)
                )
                .aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = state.detailState.reference?.name.orEmpty()
                    .replaceFirstChar { it.uppercase() },
                color = MaterialTheme.colorScheme.primary,
            )
        }

        when (state.funFactState) {
            is FunFactUiState.Success -> {
                FunFactCard(state.funFactState.funFact.content)
            }
            is FunFactUiState.Error -> {
                FunFactError { event.onRetry() }
            }
            else -> {
                FunFactShimmerCard()
            }
        }
    }
}

@Composable
fun FunFactShimmerCard() {
    val transition = rememberInfiniteTransition()
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )


    val brush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
        ),
        start = Offset(x = translateAnim - 200f, y = translateAnim - 200f),
        end = Offset(x = translateAnim, y = translateAnim)
    )

    FunFactLoadingPlaceholder(brush = brush)
}

@Composable
private fun FunFactLoadingPlaceholder(
    brush: Brush
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(brush)
        )
    }
}

@Composable
fun FunFactPlaceholderContentRow(
    height: Dp = 14.dp,
    cornerRadius: Dp = 5.dp,
    maxWidth: Float = 1.0f,
    brush: Brush
) {
    Spacer(
        modifier = Modifier
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .fillMaxWidth(fraction = maxWidth)
            .background(brush)
    )
}

@Composable
private fun FunFactCard(fact: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Fun Fact", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text(fact, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun FunFactError(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Non sono riuscito a generare un fun fact 😔")
        Spacer(Modifier.height(12.dp))
        Button(onClick = onRetry) {
            Text("Riprova")
        }
    }
}


// -- Previews -- //

@ThemePreviews
@Composable
private fun DetailPageUiPreview() = MainAppTheme {
    DetailPageUi(
        state = sampleDetailScreenState,
        event = emptyDetailEvent,
    )
}