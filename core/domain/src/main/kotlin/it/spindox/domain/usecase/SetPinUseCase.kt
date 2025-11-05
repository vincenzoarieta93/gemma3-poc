package it.spindox.domain.usecase

import it.spindox.data.repository.abstraction.DataStoreRepository
import javax.inject.Inject

class SetPinUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) {
    suspend operator fun invoke(pin: String) {
        dataStoreRepository.setPin(pin)
    }
}