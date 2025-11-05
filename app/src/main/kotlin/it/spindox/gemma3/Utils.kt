package it.spindox.gemma3

import androidx.compose.runtime.Composable
import it.spindox.data.model.ThemeAppearance
import androidx.compose.foundation.isSystemInDarkTheme


@Composable
fun isDarkTheme(themeAppearance: ThemeAppearance): Boolean {
    return when (themeAppearance) {
        ThemeAppearance.AUTO -> isSystemInDarkTheme()
        ThemeAppearance.LIGHT -> false
        ThemeAppearance.DARK -> true
    }
}