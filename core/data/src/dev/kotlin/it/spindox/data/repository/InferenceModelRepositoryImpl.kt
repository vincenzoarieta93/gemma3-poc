package it.spindox.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.ai.edge.localagents.core.proto.Content
import com.google.ai.edge.localagents.core.proto.FunctionDeclaration
import com.google.ai.edge.localagents.core.proto.GenerateContentResponse
import com.google.ai.edge.localagents.core.proto.Part
import com.google.ai.edge.localagents.core.proto.Schema
import com.google.ai.edge.localagents.core.proto.Tool
import com.google.ai.edge.localagents.core.proto.Type
import com.google.ai.edge.localagents.fc.ChatSession
import com.google.ai.edge.localagents.fc.GemmaFormatter
import com.google.ai.edge.localagents.fc.GenerativeModel
import com.google.ai.edge.localagents.fc.LlmInferenceBackend
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import it.spindox.data.exceptions.NoChatSessionException
import it.spindox.data.model.LlmModel
import it.spindox.data.model.LlmResponse
import it.spindox.data.repository.abstraction.InferenceModelRepository
import it.spindox.data.utils.EdgeFunctionUtils
import it.spindox.result.Resource
import it.spindox.result.error
import it.spindox.result.success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class InferenceModelRepositoryImpl @Inject constructor(
    private val context: Context
) : InferenceModelRepository {

    companion object {
        private const val TAG = "InferenceModelRepository"
    }

    private var model: LlmModel? = null
    private var chat: ChatSession? = null

    private val llmBackend: LlmInferenceBackend by lazy {
        val llm = LlmInference.createFromOptions(context, LlmInference.LlmInferenceOptions.builder()
            .setModelPath(getModelPath())
            .build())
        LlmInferenceBackend(llm, GemmaFormatter())
    }

    private val tool: Tool by lazy {
        Tool.newBuilder()
            .addFunctionDeclarations(switchThemeFunctionDeclaration)
            .addFunctionDeclarations(increaseDeviceVolume)
            .build()
    }

    private val generativeModel: GenerativeModel by lazy {
        val systemInstruction =Content.newBuilder()
            .setRole("system")
            .addParts(Part.newBuilder().setText(
                "You are a helpful assistant. You can call two functions:\n" +
                        "1.switchTheme() to switch the app theme to the opposite one\n" +
                        "2. increaseDeviceVolume, to increase the device volume by the given integer amount."
            ))
            .build()
        GenerativeModel(llmBackend, systemInstruction, listOf(tool))
    }

    override fun startChat() {
        chat = generativeModel.startChat()
    }

    override fun sendMessage(prompt: String): Resource<LlmResponse> {
        return try {
            chat?.let {
                success { it.sendMessage(prompt).mapToLlmResponse() }
            } ?: throw NoChatSessionException()
        } catch (e: Exception) {
            error { e }
        }
    }

    private fun GenerateContentResponse.mapToLlmResponse(): LlmResponse {
        val message = getCandidates(0).content.getParts(0)

        return if (message.hasFunctionCall()) {
            when(message.functionCall.name) {
                EdgeFunctionUtils.SWITCH_THEME_FUN_DECLARATION -> {
                    LlmResponse.SwitchThemeCall(message.functionCall.name)
                }
                EdgeFunctionUtils.INCREASE_VOLUME_FUN_DECLARATION -> {
                    val incrementLevel = try {
                        message.functionCall.args.fieldsMap[EdgeFunctionUtils.LEVEL_PROPERTY]?.stringValue?.toInt() ?: 1
                    } catch (e: Exception) {
                        Log.e(TAG, "Error while parsing level property", e)
                        1
                    }
                    LlmResponse.IncreaseDeviceVolumeCall(
                        message.functionCall.name,
                        incrementLevel
                    )
                }
                else -> {
                    LlmResponse.UnknownFunctionCall("Unknown function call: ${message.functionCall.name}")
                }
            }
        } else {
            LlmResponse.Text(message.text)
        }
    }

    override fun setModel(llmModel: LlmModel) {
        model = llmModel
    }

    override fun getModel(): LlmModel? = model

    override fun resetModel() {
        model = null
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

    // Edge Functioning

    private val switchThemeFunctionDeclaration = FunctionDeclaration.newBuilder()
        .setName(EdgeFunctionUtils.SWITCH_THEME_FUN_DECLARATION)
        .setDescription("Switch the current theme of the application to the opposite one")
        .build()

    private val increaseDeviceVolume = FunctionDeclaration.newBuilder()
        .setName(EdgeFunctionUtils.INCREASE_VOLUME_FUN_DECLARATION)
        .setDescription("Increase the device volume for the specified value. If not specified defaults to 1")
        .setParameters(
            Schema.newBuilder()
                .setType(Type.OBJECT)
                .putProperties(
                    EdgeFunctionUtils.LEVEL_PROPERTY,
                    Schema.newBuilder()
                        .setType(Type.INTEGER)
                        .setDescription("Volume increment (valid range is -10 to 10). Default 0.")
                        .build()
                )
                .build()
        )
        .build()


}