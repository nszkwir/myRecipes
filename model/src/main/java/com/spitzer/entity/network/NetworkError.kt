package com.spitzer.entity.network

import java.io.IOException

sealed class NetworkError : IOException() {
    data object NoInternet : NetworkError()
    data object NotFound : NetworkError()
    data object Unknown : NetworkError()
}
