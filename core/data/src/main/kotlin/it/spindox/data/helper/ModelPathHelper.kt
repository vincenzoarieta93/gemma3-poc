package it.spindox.data.helper

import android.content.Context
import it.spindox.data.model.LlmModel
import java.io.File
import javax.inject.Inject

class ModelPathHelper @Inject constructor(private val context: Context) {

    fun getModelPath(model: LlmModel): String {
        return model.path.takeUnless { it.isBlank() }?.let { File(context.filesDir, it).absolutePath }.orEmpty()
    }

    fun doesModelExist(model: LlmModel): Boolean {
        return File(getModelPath(model)).exists()
    }
}
