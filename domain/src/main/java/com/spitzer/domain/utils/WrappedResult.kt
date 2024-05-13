package com.spitzer.domain.utils

sealed class WrappedResult<out T, out K> {
    data class Success<T, K>(val data: T) : WrappedResult<T, K>()
    data class Error<T, K>(val exception: K) : WrappedResult<T, K>()
}
