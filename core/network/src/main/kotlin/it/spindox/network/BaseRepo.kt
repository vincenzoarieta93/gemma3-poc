package it.spindox.network

import android.util.Log
import it.spindox.result.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

abstract class BaseRepo {
    suspend fun <T> safeApiCall(apiToBeCalled: suspend () -> Response<T>): Resource<T> {
        return withContext(Dispatchers.IO) {

            try {
                val response: Response<T> = apiToBeCalled()
                if (response.isSuccessful) {
                    response.body()?.let {
                        Resource.Success(data = it)
                    } ?: run {
                        Log.e("api error", "Response body is null")
                        Resource.Error(message = "Empty response body")
                    }
                } else {
                    Log.e(
                        "api error",
                        "OkHttp API error response: ${response.code()}, description: ${response.message()}"
                    )
                    Resource.Error(message = response.code().toString())
                }

            } catch (e: HttpException) {
                Log.e("api error", "HttpException: ${e.message}")
                Resource.Error(message = e.message ?: "Something went wrong")
            } catch (e: IOException) {
                Log.e("api error", "IOException: ${e.message}")
                Resource.Error("Please check your network connection")
            } catch (e: Exception) {
                Log.e("api error", "Exception: ${e.message}")
                Resource.Error(message = e.message ?: "Something went wrong")
            }
        }
    }
}
