package it.spindox.home.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.spindox.coroutine.DefaultDispatcherProvider
import it.spindox.data.model.LlmModel
import it.spindox.domain.usecase.GetAllModelsUseCase
import it.spindox.result.Resource
import it.spindox.result.loading
import it.spindox.result.success
import it.spindox.domain.usecase.SetModelUseCase
import it.spindox.result.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dispatcherProvider: DefaultDispatcherProvider,
    private val getAllModelsUseCase: GetAllModelsUseCase,
    private val setModelUseCase: SetModelUseCase
) : ViewModel() {

    private val _uiState by lazy { MutableStateFlow(MainState()) }
    val uiState: StateFlow<MainState> = _uiState.asStateFlow()

    private val llmModels: MutableStateFlow<List<LlmModel>> = MutableStateFlow(emptyList())

    val event = MainEvent(
        onModelSelected = {}
    )

    init {
        getAllModels()
    }

    private fun getAllModels() = viewModelScope.launch(dispatcherProvider.io) {

        getAllModelsUseCase().collectLatest { modelsResource ->
            modelsResource.map { data ->
                llmModels.value = data
            }

            _uiState.update { oldState ->
                oldState.copy(
                    modelsList = when (modelsResource) {
                        is Resource.Success -> {
                            success {
                                modelsResource.data.map { item ->
                                    LlmModelUi(
                                        name = item.name,
                                        url = item.url,
                                        preferredBackend = item.preferredBackend,
                                        temperature = item.temperature,
                                        topK = item.topK,
                                        topP = item.topP,
                                        needsAuth = item.needsAuth
                                    )
                                }
                            }
                        }

                        is Resource.Error -> {
                            error(message = modelsResource.throwable)
                        }

                        is Resource.Loading -> {
                            loading()
                        }
                    }
                )
            }
        }
    }

    fun onLlmModelUiSelected(modelUi: LlmModelUi) {
        val selectedLlmModel = llmModels.value.find { it.name == modelUi.name }

        if (selectedLlmModel != null) {
            setModelUseCase(selectedLlmModel)
        }
    }
}