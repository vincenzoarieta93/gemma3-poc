package it.spindox.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.common.util.concurrent.ListenableFuture
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.google.mediapipe.tasks.genai.llminference.LlmInferenceSession
import com.google.mediapipe.tasks.genai.llminference.LlmInferenceSession.LlmInferenceSessionOptions
import com.google.mediapipe.tasks.genai.llminference.ProgressListener
import it.spindox.data.exceptions.CreateLlmInferenceTaskException
import it.spindox.data.exceptions.CreateSessionInferenceTaskException
import it.spindox.data.exceptions.InferenceEngineNotInitializedException
import it.spindox.data.exceptions.InferenceModelNotFoundException
import it.spindox.data.exceptions.ResetSessionInferenceTaskException
import it.spindox.data.model.LlmModel
import it.spindox.data.repository.abstraction.InferenceModelRepository
import it.spindox.result.Resource
import it.spindox.result.error
import it.spindox.result.success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import kotlin.math.max

class InferenceModelRepositoryImpl @Inject constructor(
    private val context: Context
) : InferenceModelRepository {

    companion object {
        /** The maximum number of tokens the model can process. */
        private const val MAX_TOKENS = 1024

        /**
         * An offset in tokens that we use to ensure that the model always has the ability to respond when
         * we compute the remaining context length.
         */
        private const val DECODE_TOKEN_OFFSET = 256

        private const val TAG = "InferenceModelRepository"
    }

    private var model: LlmModel? = null
    private var llmInference: LlmInference? = null
    private var llmInferenceSession: LlmInferenceSession? = null

    override fun setModel(llmModel: LlmModel) {
        model = llmModel
    }

    override fun getModel(): LlmModel? = model

    override fun resetModel() {
        model = null
    }

    /**
     * Initializes the inference engine and session for the currently set model.
     * This method must be called after setting a model using [setModel] and before
     * performing any inference tasks.
     *
     * It first verifies that the model file exists at the specified path. If not,
     * it throws an exception. It then proceeds to create the underlying inference engine
     * and a new inference session.
     *
     * @return A [Resource.Success] with [Unit] if initialization is successful,
     *         or a [Resource.Error] containing the exception if any step fails
     *         (e.g., model loading, session creation).
     * @throws IllegalArgumentException if the model file does not exist at the expected path.
     */
    override fun initialize(): Resource<Unit> {
        if (!doesModelExist()) {
            throw InferenceModelNotFoundException("Model not found at path: ${model?.path.orEmpty()}")
        }

        return try {
            createEngine()
            createSession()
            success {}
        } catch (e: Exception) {
            error { e }
        }
    }

    /**
     * Resets the current inference session by closing it and creating a new one.
     *
     * This is useful for clearing the conversation history and starting a fresh chat,
     * as the session maintains the context of previous interactions. The new session
     * will be created using the same model and parameters as the previous one.
     *
     * @return A [Resource.Success] if the session was reset successfully, or a
     * [Resource.Error] containing a [ResetSessionInferenceTaskException] if it fails.
     */
    override fun resetSession(): Resource<Unit> {
        return try {
            closeSessionSafely()
            llmInferenceSession = createSession()
            success { }
        } catch (e: Exception) {
            Log.e(TAG, "Session reset failed", e)
            error { ResetSessionInferenceTaskException() }
        }
    }

    /**
     * Closes and releases all resources associated with the inference engine and the current session.
     *
     * This method safely shuts down the active `LlmInferenceSession` and the underlying
     * `LlmInference` engine, freeing up memory and other system resources. It also nullifies
     * the internal references to these components to prevent memory leaks and ensure a clean state.
     *
     * It is crucial to call this method when the repository is no longer needed, for example,
     * when the component using it is destroyed.
     */
    override fun close() {
        closeSessionSafely()
        closeInferenceSafely()
    }

    /**
     * Asynchronously generates a response from the large language model for a given prompt.
     *
     * This function adds the user's [prompt] to the current conversation context
     * and then initiates the generation of a response. The generation is performed
     * asynchronously, and the result is delivered through a [ListenableFuture].
     * The [progressListener] is invoked multiple times as the model generates the
     * response token by token, allowing for real-time streaming of the output.
     *
     * @param prompt The user's input string to be sent to the model.
     * @param progressListener A listener that receives partial responses as they are generated.
     * @return A [ListenableFuture] that will complete with the final full response string.
     *         Returns `null` if the inference session has not been initialized.
     */
    override fun generateResponseAsync(
        prompt: String, progressListener: ProgressListener<String>
    ): ListenableFuture<String>? {
        return llmInferenceSession?.let {
            it.addQueryChunk(prompt)
            it.generateResponseAsync(progressListener)
        }
    }

    /**
     * Estimates the number of tokens remaining in the context window after accounting for
     * the session history and the new prompt.
     *
     * This calculation is crucial for managing the conversation context and ensuring that the
     * model has enough space to generate a meaningful response. The estimation considers:
     * - The token count of the existing conversation history ([sessionHistory]).
     * - The token count of the new user [prompt].
     * - A fixed number of "control tokens" per message, which are used internally by the model.
     * - A safety offset ([DECODE_TOKEN_OFFSET]) to reserve space for the model's reply.
     *
     * The formula is roughly:
     * `MAX_TOKENS - (tokens(history + prompt) + control_tokens + safety_offset)`
     *
     * @param sessionHistory A list of strings representing the previous messages in the conversation.
     * @param prompt The new user input string to be sent to the model.
     * @return The estimated number of remaining tokens, clamped to a minimum of 0. Returns -1
     *         if the session is not initialized or if both inputs are empty.
     */
    override fun estimateTokensRemaining(sessionHistory: List<String>, prompt: String): Int {
        return llmInferenceSession?.let { session ->
            if (prompt.isEmpty() && sessionHistory.isEmpty()) return -1

            val context = sessionHistory.joinToString(separator = "\n") { it } + "\n" + prompt

            val usedTokens = session.sizeInTokens(context)
            val controlTokens = sessionHistory.size * 3
            val remainingTokens = MAX_TOKENS - usedTokens - controlTokens - DECODE_TOKEN_OFFSET
            max(0, remainingTokens)
        } ?: -1
    }

    override fun getModelPathFromUrl(): String {
        return model?.url?.takeUnless { it.isBlank() }?.let { Uri.parse(it).lastPathSegment }
            ?.takeUnless { it.isBlank() }?.let { File(context.filesDir, it).absolutePath }.orEmpty()
    }

    override fun getModelPath(): String {
        return model?.path?.takeUnless(String::isBlank)?.takeIf { File(it).exists() }
            ?: getModelPathFromUrl()
    }

    override fun doesModelExist(): Boolean {
        return File(getModelPath()).exists()
    }

    override suspend fun deleteDownloadedModel() {
        withContext(Dispatchers.IO) {
            try {
                val outputFile = File(getModelPathFromUrl())
                if (outputFile.exists()) {
                    outputFile.delete()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error while deleting model", e)
            }
        }
    }

    /**
     * Creates and initializes the LlmInference engine.
     *
     * This function sets up the core inference engine using the options specified
     * by the currently selected `InferenceModel`. It configures the model path,
     * maximum token limit, and the preferred backend (CPU or GPU). The created
     * instance is stored in the `llmInference` property.
     *
     * @throws CreateLlmInferenceTaskException if the model fails to load or the
     * LlmInference instance cannot be created. This is typically due to an
     * invalid model file or configuration.
     */
    private fun createEngine() {
        val inferenceOptions =
            LlmInference.LlmInferenceOptions.builder().setModelPath(getModelPath())
                .setMaxTokens(MAX_TOKENS)
                .apply { model?.preferredBackend?.let { setPreferredBackend(it) } }.build()

        try {
            llmInference = LlmInference.createFromOptions(context, inferenceOptions)
        } catch (e: Exception) {
            Log.e(TAG, "Load model error: ${e.message}", e)
            throw CreateLlmInferenceTaskException()
        }
    }

    /**
     * Creates a new [LlmInferenceSession] using the current model's configuration.
     *
     * This function retrieves the inference parameters (temperature, topK, topP) from the
     * currently selected `InferenceModel` and uses them to build session options. It then
     * creates a new session from the existing [LlmInference] engine.
     *
     * @return The newly created [LlmInferenceSession].
     * @throws InferenceModelNotFoundException if no model has been selected.
     * @throws InferenceEngineNotInitializedException if the inference engine has not been initialized.
     * @throws CreateSessionInferenceTaskException if the session creation fails for other reasons.
     */
    private fun createSession(): LlmInferenceSession = try {
        val selectedModel = model ?: throw InferenceModelNotFoundException("Model not selected")
        val selectedLlmInference = llmInference ?: throw InferenceEngineNotInitializedException()

        val options = LlmInferenceSessionOptions.builder().setTemperature(selectedModel.temperature)
            .setTopK(selectedModel.topK).setTopP(selectedModel.topP).build()

        LlmInferenceSession.createFromOptions(selectedLlmInference, options)
    } catch (e: Exception) {
        Log.e(TAG, "Unable to create session", e)
        throw CreateSessionInferenceTaskException()
    }

    private fun closeSessionSafely() {
        runCatching {
            llmInferenceSession?.close()
        }.onFailure {
            Log.w(TAG, "Error while closing LLM session", it)
        }.also {
            llmInferenceSession = null
        }
    }

    private fun closeInferenceSafely() {
        runCatching {
            llmInference?.close()
        }.onFailure {
            Log.w(TAG, "Error while closing LlmInference", it)
        }.also {
            llmInference = null
        }
    }

}