package it.spindox.domain.usecase

import it.spindox.coroutine.DefaultDispatcherProvider
import it.spindox.data.repository.abstraction.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetTokenUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val dispatcherProvider: DefaultDispatcherProvider
) {
    operator fun invoke(): Flow<String> {
        return dataStoreRepository.getToken().flowOn(dispatcherProvider.io)
    }
}