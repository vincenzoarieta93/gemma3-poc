package it.spindox.domain.usecase

import it.spindox.data.repository.abstraction.DataStoreRepository
import javax.inject.Inject

class SaveTokenUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
) {
    suspend operator fun invoke(code: String) {
        dataStoreRepository.saveToken(code)
    }
}