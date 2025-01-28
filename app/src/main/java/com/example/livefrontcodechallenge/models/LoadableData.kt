package com.example.livefrontcodechallenge.models

/**
 * Data that is Loadable with multiple states
 */
sealed class LoadableData<out T> {
    data class Data<T>(val data: T) : LoadableData<T>()
    data object Loading : LoadableData<Nothing>()
    data object Empty : LoadableData<Nothing>()
    data class Error(val error: Throwable) : LoadableData<Nothing>()
}
