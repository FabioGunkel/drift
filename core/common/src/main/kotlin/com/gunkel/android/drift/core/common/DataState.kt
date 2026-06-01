package com.gunkel.android.drift.core.common

sealed class DataState<out T> {
    data object Loading : DataState<Nothing>()
    data class Success<out T>(val data: T) : DataState<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : DataState<Nothing>()
}
