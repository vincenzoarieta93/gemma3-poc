package it.spindox.domain.usecase

import it.spindox.coroutine.DefaultDispatcherProvider
import it.spindox.data.model.LlmModel
import it.spindox.data.model.LlmModelDownloadState
import it.spindox.data.repository.abstraction.DataStoreRepository
import it.spindox.data.repository.abstraction.LlmModelDownloadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DownloadLlmModelUseCase @Inject constructor(
    private val repository: LlmModelDownloadRepository,
    private val dispatcherProvider: DefaultDispatcherProvider
) {
    operator fun invoke(llmModel: LlmModel, outputFilePath: String): Flow<LlmModelDownloadState> {
        return repository.downloadModel(llmModel, outputFilePath)
    }
}