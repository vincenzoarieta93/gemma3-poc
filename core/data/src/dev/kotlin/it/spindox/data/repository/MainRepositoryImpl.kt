package it.spindox.data.repository

import it.spindox.data.datasource.ModelsDataSource
import it.spindox.data.model.LlmModel
import it.spindox.data.repository.abstraction.MainRepository
import it.spindox.result.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class MainRepositoryImpl @Inject constructor(
    private val modelsDataSource: ModelsDataSource,
) : MainRepository {

    override fun getAllModels(): Flow<Resource<List<LlmModel>>> {
        return flow {
            emit(modelsDataSource.getAllModels())
        }
    }
}