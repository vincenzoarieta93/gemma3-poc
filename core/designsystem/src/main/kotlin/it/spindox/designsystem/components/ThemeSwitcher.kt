package it.spindox.designsystem.components

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.ui.tooling.preview.Preview
import it.spindox.designsystem.utils.stayPositive

@Composable
fun ThemeSwitcher(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    size: Dp = 40.dp,
    onClick: () -> Unit,
) {
    val iconSize: Dp = size / 3
    val padding: Dp = size / 12

    val offset by animateFloatAsState(
        targetValue = if (isDarkTheme) 0f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "offset"
    )

    // -- Container --
    Box(modifier = modifier
        .width(size * 2)
        .height(size)
        .clip(shape = CircleShape)
        .clickable { onClick() }
        .background(MaterialTheme.colorScheme.secondaryContainer)
        .border(
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary
            ),
            shape = CircleShape
        )
    ) {
        // -- Selector --
        Row {
            Spacer(Modifier.weight(offset.stayPositive()))
            Box(
                modifier = Modifier
                    .size(size)
                    .padding(all = padding)
                    .clip(shape = CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {

            }
            Spacer(Modifier.weight((1f-offset).stayPositive()))
        }

        // -- Icons --
        Row(
            modifier = Modifier

        ) {
            Box(
                modifier = Modifier.size(size),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(iconSize),
                    imageVector = Icons.Filled.Nightlight,
                    contentDescription = "Theme Icon",
                    tint = if (isDarkTheme) MaterialTheme.colorScheme.secondaryContainer
                    else MaterialTheme.colorScheme.primary
                )
            }
            Box(
                modifier = Modifier.size(size),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(iconSize),
                    imageVector = Icons.Filled.LightMode,
                    contentDescription = "Theme Icon",
                    tint = if (isDarkTheme) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondaryContainer
                )
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Light theme")
@Composable
fun ThemeSwitcherPreview() = it.spindox.designsystem.theme.MainAppTheme {
    ThemeSwitcher(isDarkTheme = false) { }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark theme")
@Composable
fun ThemeSwitcherDarkPreview() = it.spindox.designsystem.theme.MainAppTheme {
    ThemeSwitcher(isDarkTheme = true) { }
}
