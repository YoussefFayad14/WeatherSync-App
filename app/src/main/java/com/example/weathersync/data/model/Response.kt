package com.example.weathersync.data.model

sealed class Response<out T> {
    data object Loading : Response<Nothing>()
    data class Success<T>(val data: T) : Response<T>()
    data class Failure(val error: Throwable) : Response<Nothing>()
}
