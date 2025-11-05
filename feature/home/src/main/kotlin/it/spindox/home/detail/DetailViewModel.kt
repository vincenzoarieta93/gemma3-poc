package it.spindox.home.detail

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.spindox.coroutine.DefaultDispatcherProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class DetailViewModel @Inject constructor(
    private val dispatcherProvider: DefaultDispatcherProvider,
) : ViewModel() {

    private val _uiState by lazy {
        MutableStateFlow(
            DetailScreenState(
                detailState = DetailState(),
                funFactState = FunFactUiState.Loading
            )
        )
    }

    val uiState: StateFlow<DetailScreenState> = _uiState.asStateFlow()
}