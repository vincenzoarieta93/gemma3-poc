package it.spindox.gemma3

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.spindox.data.model.ThemeAppearance
import it.spindox.domain.usecase.GetThemeUseCase
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val getThemeUseCase: GetThemeUseCase,
) : ViewModel() {

    fun getThemeAppearance(): StateFlow<ThemeAppearance> {
        return getThemeUseCase()
    }
}