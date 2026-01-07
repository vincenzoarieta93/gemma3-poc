package it.spindox.domain.usecase

import it.spindox.data.repository.abstraction.InferenceModelRepository
import javax.inject.Inject

class CheckModelFileUseCase @Inject constructor(
    private val repository: InferenceModelRepository
) {
    operator fun invoke(): Boolean {
        return repository.doesModelExist()
    }
}