package it.spindox.data.repository.abstraction

import com.google.common.util.concurrent.ListenableFuture
import com.google.mediapipe.tasks.genai.llminference.ProgressListener
import it.spindox.data.model.LlmModel
import it.spindox.result.Resource

interface InferenceModelRepository {

    fun setModel(llmModel: LlmModel)
    fun resetModel()
    fun getModelPathFromUrl(): String
    fun doesModelExist(): Boolean

    fun initialize(): Resource<Unit>
    fun resetSession(): Resource<Unit>
    fun close()

    fun generateResponseAsync(prompt: String, progressListener: ProgressListener<String>): ListenableFuture<String>?
    fun estimateTokensRemaining(sessionHistory: List<String>, prompt: String): Int
}