package it.spindox.home.main

import com.google.mediapipe.tasks.genai.llminference.LlmInference
import it.spindox.result.Resource
import it.spindox.result.loading
import it.spindox.result.success


data class MainState (
    val modelsList: Resource<List<LlmModelUi>> = loading(),
    val isDarkTheme: Boolean = false,
)

data class LlmModelUi (
    val name: String,
    val url: String,
    val preferredBackend: LlmInference.Backend?
)

val sampleMainState = MainState (
    modelsList = success {
        listOf(
            LlmModelUi(name = "GEMMA3_1B_IT_CPU", "https://pokeapi.co/api/v2/pokemon/1", preferredBackend = LlmInference.Backend.CPU),
            LlmModelUi(name = "GEMMA_3_1B_IT_GPU", "https://pokeapi.co/api/v2/pokemon/4/", preferredBackend = LlmInference.Backend.GPU),
            LlmModelUi(name = "GEMMA_2_2B_IT_CPU", "https://pokeapi.co/api/v2/pokemon/7/", preferredBackend = LlmInference.Backend.CPU),
            LlmModelUi(name = "DEEPSEEK_R1_DISTILL_QWEN_1_5_B", "https://pokeapi.co/api/v2/pokemon/2/", preferredBackend = LlmInference.Backend.CPU),
        )
    },
)