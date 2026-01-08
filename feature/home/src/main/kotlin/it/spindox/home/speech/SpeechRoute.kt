package it.spindox.home.speech

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SpeechRoute(
    viewModel: SpeechViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    SpeechScreen(
        state = state,
        onStartListening = { viewModel.startListening() },
        onStopListening = { viewModel.stopListening() }
    )
}


@Composable
fun SpeechScreen(
    state: SpeechUiState, onStartListening: () -> Unit, onStopListening: () -> Unit
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
                .fillMaxSize()
                .padding(24.dp)
        ) {

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                MicWithWaveform(
                    isListening = state.isListening,
                    audioLevel = state.audioLevel ?: 0f
                )

                Spacer(modifier = Modifier.height(24.dp))

                SpeechText(
                    text = state.recognizedText, isListening = state.isListening
                )
            }

            SpeechFab(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp),
                isListening = state.isListening,
                onStart = { onStartListening() },
                onStop = { onStopListening() })
        }
    }
}

@Composable
fun MicWithWaveform(
    isListening: Boolean,
    audioLevel: Float
) {
    Box(contentAlignment = Alignment.Center) {

        if (isListening) {
            AudioWaveform(audioLevel)
        }

        PulsingMic(isListening = isListening)
    }
}


@Composable
fun AudioWaveform(
    audioLevel: Float?,
    modifier: Modifier = Modifier
) {
    val normalized = ((audioLevel ?: 0f) / 10f).coerceIn(0f, 1f)

    val animatedScale by animateFloatAsState(
        targetValue = 1f + normalized * 0.8f,
        animationSpec = tween(80),
        label = "wave"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .scale(animatedScale)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                    CircleShape
                )
        )
    }
}


@Composable
fun PulsingMic(
    isListening: Boolean, size: Dp = 96.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.6f, animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200, easing = FastOutSlowInEasing
            ), repeatMode = RepeatMode.Restart
        ), label = "scale"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 0f, animationSpec = infiniteRepeatable(
            animation = tween(1200), repeatMode = RepeatMode.Restart
        ), label = "alpha"
    )

    Box(
        contentAlignment = Alignment.Center
    ) {
        if (isListening) {
            Box(
                modifier = Modifier
                    .size(size)
                    .scale(pulseScale)
                    .alpha(pulseAlpha)
                    .background(
                        color = MaterialTheme.colorScheme.primary, shape = CircleShape
                    )
            )
        }

        Box(
            modifier = Modifier
                .size(size)
                .background(
                    color = MaterialTheme.colorScheme.primary, shape = CircleShape
                ), contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
fun SpeechText(
    text: String, isListening: Boolean
) {
    val isEmpty = text.isBlank()

    Text(
        text = if (isEmpty) "Listening..." else text,
        style = MaterialTheme.typography.bodyLarge,
        color = if (isEmpty) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        else MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center
    )
}

@Composable
fun SpeechFab(
    modifier: Modifier = Modifier, isListening: Boolean, onStart: () -> Unit, onStop: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    FloatingActionButton(
        modifier = modifier, onClick = {
            if (isListening) {
                haptic.performHapticFeedback(
                    HapticFeedbackType.TextHandleMove
                )
                onStop()
            } else {
                haptic.performHapticFeedback(
                    HapticFeedbackType.LongPress
                )
                onStart()
            }
        }) {
        Icon(
            imageVector = if (isListening) Icons.Default.ArrowUpward
            else Icons.Default.Mic, contentDescription = null
        )
    }
}


@Preview
@Composable
fun SpeechScreenPreview() {
    SpeechScreen(
        state = SpeechUiState(
            isListening = true,
            recognizedText = "Hello, how are you?",
            audioLevel = 0f
        ), onStartListening = {},
        onStopListening = {}
    )
}


