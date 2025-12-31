package it.spindox.data.repository.abstraction

import it.spindox.data.model.LlmModel
import it.spindox.result.Resource
import kotlinx.coroutines.flow.Flow

interface MainRepository {
    fun getAllModels(): Flow<Resource<List<LlmModel>>>
}