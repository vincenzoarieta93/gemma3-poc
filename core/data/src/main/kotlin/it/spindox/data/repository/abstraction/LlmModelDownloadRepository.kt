package it.spindox.data.repository.abstraction

import it.spindox.data.model.LlmModel
import it.spindox.data.model.LlmModelDownloadState
import kotlinx.coroutines.flow.Flow

interface LlmModelDownloadRepository {
    fun downloadModel(model: LlmModel, outputFilePath: String): Flow<LlmModelDownloadState>
}