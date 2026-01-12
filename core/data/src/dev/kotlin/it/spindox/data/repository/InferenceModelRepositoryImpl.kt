package it.spindox.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import it.spindox.data.helper.ModelPathHelper
import it.spindox.data.model.LlmModel
import it.spindox.data.model.LlmResponse
import it.spindox.data.repository.abstraction.InferenceModelRepository
import it.spindox.data.utils.EdgeFunctionUtils
import it.spindox.result.Resource
import it.spindox.result.error
import it.spindox.result.loading
import it.spindox.result.success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import javax.inject.Inject

class InferenceModelRepositoryImpl @Inject constructor(
    private val context: Context,
    private val modelPathHelper: ModelPathHelper
) : InferenceModelRepository {

    companion object {
        private const val TAG = "InferenceModelRepository"
    }

    private var model: LlmModel? = null
    private var llmInference: LlmInference? = null

    override fun startChat() {
        llmInference = LlmInference.createFromOptions(
            context,
            LlmInference.LlmInferenceOptions.builder()
                .setMaxTopK(64)
                .apply { model?.preferredBackend?.let { setPreferredBackend(it) } }
                .setModelPath(getModelPath())
                .build()
        )
    }

    override fun sendMessage(prompt: String): Flow<Resource<LlmResponse>> = callbackFlow {
        val inference = llmInference ?: run {
            trySend(error { IllegalStateException("LLM not initialized") })
            close()
            return@callbackFlow
        }

        val buffer = StringBuilder()
        inference.generateResponseAsync(prompt.concatInstructionsToPrompt()) { partialResult, done ->
            when {
                !done -> {
                    buffer.append(partialResult)
                    trySend(loading())
                }

                else -> {
                    val finalText = buffer.toString()
                    if (finalText.isBlank()) {
                        trySend(error { RuntimeException("Empty response") })
                    } else {
                        val parsedModelResponse = parseModelResponse(finalText)
                        if (parsedModelResponse == null) {
                            trySend(error { RuntimeException("Invalid response") })
                        } else {
                            trySend(success { parsedModelResponse.mapToLlmResponse() })
                        }
                    }
                    close()
                }
            }
        }
        awaitClose {}
    }

    data class FunctionCall(
        val name: String, val parameters: Map<String, Any>
    )

    fun parseModelResponse(responseJson: String): FunctionCall? {
        return try {
            val jsonObj = JSONObject(responseJson)

            // Estrai il nome
            val name = jsonObj.getString("name")

            // Estrai i parametri
            val parameters = mutableMapOf<String, Any>()
            val paramsObj = jsonObj.optJSONObject("parameters")
            paramsObj?.keys()?.forEach { key ->
                // Converte valori numerici in Int se possibile
                val parsedValue: Any = when (val value = paramsObj.get(key)) {
                    is Int -> value
                    is String -> value
                    else -> value.toString()
                }
                parameters[key] = parsedValue
            }

            FunctionCall(name, parameters)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    private fun FunctionCall?.mapToLlmResponse(): LlmResponse {
        if (this == null) {
            return LlmResponse.UnknownFunctionCall("Unknown function call")
        }

        return when (this.name) {
            EdgeFunctionUtils.SWITCH_THEME_FUN_DECLARATION -> {
                Log.d(TAG, "Switching theme")
                LlmResponse.SwitchThemeCall
            }

            EdgeFunctionUtils.NAVIGATE_TO_DESTINATION_FUN_DECLARATION -> {
                val destination: String = try {
                    (this.parameters[EdgeFunctionUtils.DESTINATION_PROPERTY] as? String).orEmpty()
                } catch (e: Exception) {
                    Log.e(TAG, "Error while parsing destination property", e)
                    ""
                }
                Log.d(TAG, "Navigating to destination: $destination")
                LlmResponse.NavigateToDestination(
                    destination
                )
            }

            EdgeFunctionUtils.OPEN_SYSTEM_SETTINGS_FUN_DECLARATION -> {
                val settingsScreen: String = try {
                    (this.parameters[EdgeFunctionUtils.SETTINGS_SCREEN_PROPERTY] as? String).orEmpty()
                } catch (e: Exception) {
                    Log.e(TAG, "Error while parsing settings screen property", e)
                    ""
                }
                Log.d(TAG, "Settings screen: $settingsScreen")
                LlmResponse.OpenWiFiSettingsScreen
            }

            else -> {
                LlmResponse.UnknownFunctionCall("Unknown function call: ${this.name}")
            }
        }
    }

    override fun setModel(llmModel: LlmModel) {
        model = llmModel
    }

    override fun getModel(): LlmModel? = model

    override fun resetModel() {
        model = null
    }

    override fun getModelPath(): String {
        return model?.let {
            modelPathHelper.getModelPath(it)
        }.orEmpty()
    }

    override fun doesModelExist(): Boolean {
        return model?.let { modelPathHelper.doesModelExist(it) } ?: false
    }

    override suspend fun deleteDownloadedModel() {
        withContext(Dispatchers.IO) {
            try {
                val outputFile = File(getModelPath())
                if (outputFile.exists()) {
                    outputFile.delete()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error while deleting model", e)
            }
        }
    }

    fun String.concatInstructionsToPrompt(): String {
        val functionCallingPrompt = "You have access to functions. If you decide to invoke any of the function(s),\n" +
                "you MUST put it in the format of\n" +
                "{\"name\": function name, \"parameters\": dictionary of argument name and its value}\n" +
                "\n" +
                "You SHOULD NOT include any other text in the response if you call a function\n" +
                "[\n" +
                "  {\n" +
                "    \"name\": \"navigate_to_destination\",\n" +
                "    \"description\": \"Navigate to a specific destination in the app\",\n" +
                "    \"parameters\": {\n" +
                "      \"type\": \"object\",\n" +
                "      \"properties\": {\n" +
                "        \"DESTINATION\": {\n" +
                "          \"type\": \"STRING\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"required\": [\n" +
                "        \"DESTINATION\"\n" +
                "      ]\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"switch_app_theme\",\n" +
                "    \"description\": \"Switch the current theme of the app to the opposite one\",\n" +
                "    \"parameters\": {\n" +
                "      \"type\": \"object\",\n" +
                "      \"properties\": {\n" +
                "        \"THEME\": {\n" +
                "          \"type\": \"STRING\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"required\": [\n" +
                "        \"THEME\"\n" +
                "      ]\n" +
                "    }\n" +
                "  }\n" +
//                "  {\n" +
//                "    \"name\": \"open_system_settings_screen\",\n" +
//                "    \"description\": \"Open a specific settings screen of the system\",\n" +
//                "    \"parameters\": {\n" +
//                "      \"type\": \"object\",\n" +
//                "      \"properties\": {\n" +
//                "        \"SETTINGS_SCREEN\": {\n" +
//                "          \"type\": \"STRING\"\n" +
//                "        }\n" +
//                "      },\n" +
//                "      \"required\": [\n" +
//                "        \"SETTINGS_SCREEN\"\n" +
//                "      ]\n" +
//                "    }\n" +
//                "  },\n" +
                "]"
        return "$functionCallingPrompt\n${this}"
    }


}