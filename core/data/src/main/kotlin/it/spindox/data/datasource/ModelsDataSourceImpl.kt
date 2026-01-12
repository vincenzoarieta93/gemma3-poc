package it.spindox.data.datasource

import com.google.mediapipe.tasks.genai.llminference.LlmInference.Backend
import it.spindox.data.model.LlmModel
import it.spindox.result.Resource
import javax.inject.Inject

class ModelsDataSourceImpl @Inject constructor() : ModelsDataSource {

    private val GEMMA3_270M_IT_CPU = LlmModel(
        name = "GEMMA3_270M_IT_CPU",
        path = "gemma3-270m-it-q8.task",
        url = "https://huggingface.co/litert-community/gemma-3-270m-it/resolve/main/gemma3-270m-it-q8.task",
        licenseUrl = "https://huggingface.co/litert-community/gemma-3-270m-it",
        needsAuth = true,
        preferredBackend = Backend.CPU,
        thinking = false,
        temperature = 1.0f,
        topK = 64,
        topP = 0.95f
    )
    private val GEMMA3_1B_IT_CPU = LlmModel(
        name = "GEMMA3_1B_IT_CPU",
        path = "Gemma3-1B-IT_multi-prefill-seq_q8_ekv2048.task",
        url = "https://huggingface.co/litert-community/Gemma3-1B-IT/resolve/main/Gemma3-1B-IT_multi-prefill-seq_q8_ekv2048.task",
        licenseUrl = "https://huggingface.co/litert-community/Gemma3-1B-IT",
        needsAuth = true,
        preferredBackend = Backend.CPU,
        thinking = false,
        temperature = 1.0f,
        topK = 64,
        topP = 0.95f
    )
    private val GEMMA_3_1B_IT_GPU = LlmModel(
        name = "GEMMA_3_1B_IT_GPU",
        path = "Gemma3-1B-IT_multi-prefill-seq_q8_ekv2048_gpu.task",
        url = "https://huggingface.co/litert-community/Gemma3-1B-IT/resolve/main/Gemma3-1B-IT_multi-prefill-seq_q8_ekv2048.task",
        licenseUrl = "https://huggingface.co/litert-community/Gemma3-1B-IT",
        needsAuth = true,
        preferredBackend = Backend.GPU,
        thinking = false,
        temperature = 1.0f,
        topK = 64,
        topP = 0.95f
    )
    private val GEMMA_2_2B_IT_CPU = LlmModel(
        name = "GEMMA_2_2B_IT_CPU",
        path = "Gemma2-2B-IT_multi-prefill-seq_q8_ekv1280.task",
        url = "https://huggingface.co/litert-community/Gemma2-2B-IT/resolve/main/Gemma2-2B-IT_multi-prefill-seq_q8_ekv1280.task",
        licenseUrl = "https://huggingface.co/litert-community/Gemma2-2B-IT",
        needsAuth = true,
        preferredBackend = Backend.CPU,
        thinking = false,
        temperature = 0.6f,
        topK = 50,
        topP = 0.9f
    )

    private val DEEPSEEK_R1_DISTILL_QWEN_1_5_B = LlmModel(
        name = "DEEPSEEK_R1_DISTILL_QWEN_1_5_B",
        path = "DeepSeek-R1-Distill-Qwen-1.5B_multi-prefill-seq_q8_ekv1280.task",
        url = "https://huggingface.co/litert-community/DeepSeek-R1-Distill-Qwen-1.5B/resolve/main/DeepSeek-R1-Distill-Qwen-1.5B_multi-prefill-seq_q8_ekv1280.task",
        licenseUrl = "",
        needsAuth = false,
        preferredBackend = Backend.CPU,
        thinking = true,
        temperature = 0.6f,
        topK = 40,
        topP = 0.7f
    )

    private val LLAMA_3_2_1B_INSTRUCT = LlmModel(
        name = "LLAMA_3_2_1B_INSTRUCT",
        path = "Llama-3.2-1B-Instruct_multi-prefill-seq_q8_ekv1280.task",
        url = "https://huggingface.co/litert-community/Llama-3.2-1B-Instruct/resolve/main/Llama-3.2-1B-Instruct_multi-prefill-seq_q8_ekv1280.task",
        licenseUrl = "https://huggingface.co/litert-community/Llama-3.2-1B-Instruct",
        needsAuth = true,
        preferredBackend = Backend.CPU,
        thinking = false,
        temperature = 0.6f,
        topK = 64,
        topP = 0.9f
    )

    override suspend fun getAllModels(): Resource<List<LlmModel>> {
        return Resource.Success(
            listOf(
                GEMMA3_270M_IT_CPU,
                GEMMA3_1B_IT_CPU,
                GEMMA_3_1B_IT_GPU,
                GEMMA_2_2B_IT_CPU,
            )
        )
    }
}