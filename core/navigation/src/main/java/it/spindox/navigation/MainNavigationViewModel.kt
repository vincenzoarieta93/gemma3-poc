package it.spindox.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainNavigationViewModel @Inject constructor() : ViewModel() {
    private val _onModelDownloadedEvent: MutableSharedFlow<Unit> = MutableSharedFlow(replay = 1)
    val onModelDownloadedEvent: MutableSharedFlow<Unit> = _onModelDownloadedEvent

    fun triggerModelDownloadedEvent() {
        viewModelScope.launch {
            _onModelDownloadedEvent.emit(Unit)
        }
    }

}