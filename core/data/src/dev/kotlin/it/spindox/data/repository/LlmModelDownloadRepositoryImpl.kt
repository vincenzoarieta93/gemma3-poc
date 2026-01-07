package it.spindox.data.repository

import android.util.Log
import it.spindox.data.exceptions.MissingAccessTokenException
import it.spindox.data.exceptions.UnauthorizedAccessException
import it.spindox.data.model.LlmModel
import it.spindox.data.model.LlmModelDownloadState
import it.spindox.data.repository.abstraction.LlmModelDownloadRepository
import it.spindox.datastore.datasource.abstraction.DataStoreDataSource
import it.spindox.network.di.qualifiers.LlmHttpClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class LlmModelDownloadRepositoryImpl @Inject constructor(
    @LlmHttpClient private val client: OkHttpClient,
    private val dataStore: DataStoreDataSource,
) : LlmModelDownloadRepository {

    override fun downloadModel(
        model: LlmModel,
        outputFilePath: String
    ): Flow<LlmModelDownloadState> = flow {
        val requestBuilder = Request.Builder().url(model.url)

        if (model.needsAuth) {
            val token = dataStore.getToken().first().ifBlank { throw MissingAccessTokenException() }
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        val outputFile = File(outputFilePath)
        Log.d("GINO", "Preparing to download...")

        client.newCall(requestBuilder.build()).execute().use { response ->
            Log.d("GINO", "Downloaded!!!")

            if (!response.isSuccessful) {
                if (response.code == 401) {
                    dataStore.removeToken()
                    throw UnauthorizedAccessException()
                }

                throw Exception("HTTP ${response.code}")
            }

            val body = response.body ?: throw IllegalStateException("Empty body")

            body.byteStream().use { input ->
                FileOutputStream(outputFile).use { output ->
                    val buffer = ByteArray(8 * 11024)
                    var bytesRead: Int
                    var totalBytesRead = 0L
                    val contentLength = body.contentLength()

                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        totalBytesRead += bytesRead

                        if (contentLength > 0) {
                            emit(
                                LlmModelDownloadState.DownloadInProgress(
                                    ((totalBytesRead * 100) / contentLength).toInt()
                                )
                            )
                        }
                    }
                    output.flush()
                }
            }

            emit(LlmModelDownloadState.DownloadComplete(outputFile))
        }
    }.catch { emit(LlmModelDownloadState.DownloadFailure(it)) }
}