package it.spindox.domain.usecase

import it.spindox.data.repository.abstraction.InferenceModelRepository
import javax.inject.Inject

class DeleteModelFileUseCase @Inject constructor(
    private val repository: InferenceModelRepository
) {
    suspend operator fun invoke() {
        return repository.deleteDownloadedModel()
    }
}