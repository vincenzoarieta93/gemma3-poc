package it.spindox.domain.usecase

import it.spindox.data.model.FunctionCallEvent
import it.spindox.data.model.LlmResponse
import it.spindox.data.repository.abstraction.InferenceModelRepository
import it.spindox.result.Resource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val repository: InferenceModelRepository
) {
    private val _events = MutableSharedFlow<FunctionCallEvent>()
    val events: SharedFlow<FunctionCallEvent> = _events.asSharedFlow()

    suspend operator fun invoke(prompt: String) {
        val response = repository.sendMessage(prompt)
        if (response is Resource.Error) {
            _events.emit(FunctionCallEvent.Error(response.getErrorMessage()))
        } else {
            when (val llmResponse = (response as Resource.Success).data) {
                is LlmResponse.Text -> {
                    llmResponse.text
                }

                is LlmResponse.SwitchThemeCall -> {
                    _events.emit(FunctionCallEvent.SwitchTheme)
                }

                is LlmResponse.NavigateToDestination -> {
                    _events.emit(FunctionCallEvent.NavigateToDestination(llmResponse.destination))
                }

                is LlmResponse.UnknownFunctionCall -> {
                    _events.emit(FunctionCallEvent.Error(llmResponse.message))
                }
            }
        }
    }
}