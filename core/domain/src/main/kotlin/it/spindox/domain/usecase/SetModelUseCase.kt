package it.spindox.domain.usecase

import it.spindox.data.model.LlmModel
import it.spindox.data.repository.abstraction.InferenceModelRepository
import javax.inject.Inject

class SetModelUseCase @Inject constructor(
    private val repository: InferenceModelRepository
) {
    operator fun invoke(model: LlmModel): Unit = repository.setModel(model)
}