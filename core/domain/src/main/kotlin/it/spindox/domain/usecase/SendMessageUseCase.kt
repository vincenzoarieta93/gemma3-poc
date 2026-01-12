package it.spindox.domain.usecase

import it.spindox.coroutine.DefaultDispatcherProvider
import it.spindox.data.model.LlmResponse
import it.spindox.data.repository.abstraction.InferenceModelRepository
import it.spindox.result.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val dispatcherProvider: DefaultDispatcherProvider,
    private val repository: InferenceModelRepository
) {
    operator fun invoke(prompt: String): Flow<Resource<LlmResponse>> = repository.sendMessage(prompt).flowOn(dispatcherProvider.default)
}