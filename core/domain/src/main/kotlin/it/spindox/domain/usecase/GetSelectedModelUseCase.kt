package it.spindox.domain.usecase

import it.spindox.data.repository.abstraction.InferenceModelRepository
import model.LlmModelWithFilePath
import javax.inject.Inject

class GetSelectedModelUseCase @Inject constructor(
    private val repository: InferenceModelRepository
) {
    operator fun invoke(): LlmModelWithFilePath? {
        return repository.getModel()?.let {
            LlmModelWithFilePath(it, repository.getModelPath(), repository.getModelPathFromUrl())
        }
    }
}