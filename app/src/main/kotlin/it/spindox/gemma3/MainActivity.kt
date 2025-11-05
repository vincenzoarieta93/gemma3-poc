package it.spindox.gemma3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dagger.hilt.android.AndroidEntryPoint
import it.spindox.designsystem.theme.MainAppTheme
import it.spindox.gemma3.ui.navigation.MainNavigation


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            val themeAppearance by viewModel.getThemeAppearance().collectAsState()

            MainAppTheme(
                isDarkTheme(
                    themeAppearance
                )
            ) {
                MainNavigation()
            }
        }
    }
}