package it.spindox.data.helper

import android.content.Context
import androidx.core.net.toUri
import it.spindox.data.model.LlmModel
import java.io.File
import javax.inject.Inject

class ModelPathHelper @Inject constructor(private val context: Context) {

    fun getModelPath(model: LlmModel): String {
        return model.path.takeUnless(String::isBlank)?.takeIf { File(it).exists() }
            ?: getModelPathFromUrl(model)
    }

    fun doesModelExist(model: LlmModel): Boolean {
        return File(getModelPath(model)).exists()
    }


    fun getModelPathFromUrl(model: LlmModel): String {
        return model.url.takeUnless { it.isBlank() }?.toUri()?.lastPathSegment
            ?.takeUnless { it.isBlank() }?.let { File(context.filesDir, it).absolutePath }.orEmpty()
    }
}
