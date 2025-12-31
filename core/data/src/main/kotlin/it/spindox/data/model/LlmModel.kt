package it.spindox.data.model

import com.google.mediapipe.tasks.genai.llminference.LlmInference.Backend

data class LlmModel(
    val name: String,
    val path: String,
    val url: String,
    val licenseUrl: String,
    val needsAuth: Boolean,
    val preferredBackend: Backend?,
    val thinking: Boolean,
    val temperature: Float,
    val topK: Int,
    val topP: Float,
)