package model

import it.spindox.data.model.LlmModel

data class LlmModelWithFilePath(val model: LlmModel, val modelPath: String, val modelPathFromUrl: String)