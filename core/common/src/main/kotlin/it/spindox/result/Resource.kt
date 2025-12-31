package it.spindox.result

import java.lang.RuntimeException

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val throwable: Throwable) : Resource<Nothing>() {
        fun getErrorMessage(): String {
            return throwable.message ?: "Generic error"
        }
    }

    data object Loading : Resource<Nothing>()

    companion object {
        val DEFAULT_ERROR = RuntimeException("Resource Failed to load.")
    }
}

//FIXME and help me 😭
/*
fun <T> Flow<T>.asResult(): Flow<Resource<T>> = map<T, Resource<T>> { Resource.Success(it) }
    .onStart { emit(Resource.Loading) }
    .catch { emit(Resource.Error(it.message ?: "generic error")) }
*/

/**
 * Convenience method for transforming a [Resource] of type [T] into a [Resource] of type [O].
 *
 * This extension function maps a [Resource] of type [T] into a [Resource] if type [O] by means of
 * a transform function provided as functional type argument.
 *
 * Example of usage:
 *
 * ```
 * val intRes = success { 10 }
 * val stringRes = intRes.map { it.toString() }
 * ```
 *
 * @receiver A [Resource] of type [T] to be transformed.
 * @param transform A lambda function that takes an object of type [T] as input and returns an object
 * of type [O] as output.
 * @return A [Resource] of type [O]
 */
inline fun <T, O> Resource<T>.map(transform: (T) -> O): Resource<O> {
    return when (this) {
        is Resource.Success -> success { transform(data) }
        is Resource.Error -> error { throwable }
        Resource.Loading -> loading()
    }
}

inline fun <T> success(dataProducer: () -> T): Resource<T> {
    return Resource.Success(dataProducer())
}

inline fun <T> error(errorProducer: () -> Throwable = { Resource.DEFAULT_ERROR }): Resource<T> {
    return Resource.Error(errorProducer())
}

fun <T> loading(): Resource<T> {
    return Resource.Loading
}
