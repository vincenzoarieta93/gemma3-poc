package it.spindox.data.repository.abstraction

import it.spindox.data.model.LlmModel
import it.spindox.data.model.LlmResponse
import it.spindox.result.Resource

interface InferenceModelRepository {

    fun setModel(llmModel: LlmModel)
    fun getModel(): LlmModel?
    fun resetModel()
    fun getModelPathFromUrl(): String
    fun getModelPath(): String
    fun doesModelExist(): Boolean

    fun startChat()
    fun sendMessage(prompt: String): Resource<LlmResponse>

    suspend fun deleteDownloadedModel()
}