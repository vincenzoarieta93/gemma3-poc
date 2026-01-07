package it.spindox.domain.usecase

import it.spindox.data.repository.abstraction.DataStoreRepository
import javax.inject.Inject

class RemoveTokenUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) {
    suspend operator fun invoke() {
        dataStoreRepository.removeToken()
    }
}