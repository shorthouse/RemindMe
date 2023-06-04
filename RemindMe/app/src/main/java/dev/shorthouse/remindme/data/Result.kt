package dev.shorthouse.remindme.data

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    object Error : Result<Nothing>()
}
