package dev.shorthouse.remindme.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

sealed class Response<out R> {
    data class Success<out T>(val data: T) : Response<T>()
    object Error : Response<Nothing>()
}

inline fun <T> resultFrom(block: () -> T): Response<T> {
    return try {
        val result = block()
        Response.Success(result)
    } catch (e: Exception) {
        Response.Error
    }
}

fun <T> Flow<T>.asResult(): Flow<Response<T>> {
    return this
        .map<T, Response<T>> {
            Response.Success(it)
        }
        .catch { emit(Response.Error) }
}
