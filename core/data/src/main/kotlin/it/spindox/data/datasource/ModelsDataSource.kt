package it.spindox.data.datasource

import it.spindox.data.model.LlmModel
import it.spindox.result.Resource

interface ModelsDataSource {
    suspend fun getAllModels(): Resource<List<LlmModel>>
}