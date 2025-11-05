package it.spindox.domain.usecase

import it.spindox.coroutine.DefaultDispatcherProvider
import it.spindox.data.repository.abstraction.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ValidatePinUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val dispatcherProvider: DefaultDispatcherProvider
) {
    operator fun invoke(pin: String): Flow<Boolean> {
        return dataStoreRepository.validatePin(pin).flowOn(dispatcherProvider.io)
    }
}