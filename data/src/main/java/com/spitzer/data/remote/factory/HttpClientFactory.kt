package com.spitzer.data.remote.factory

import android.content.Context
import com.spitzer.data.remote.interceptor.APIKeyRequestInterceptor
import com.spitzer.data.remote.interceptor.NetworkConnectionInterceptor
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class HTTPClientFactory(
    @ApplicationContext private val applicationContext: Context
) {
    private var okHttpClient: OkHttpClient? = null
    val client: OkHttpClient
        get() {
            okHttpClient?.let { return it }

            val timeout: Long = 30
            val okHttpClient = OkHttpClient().newBuilder()
                .retryOnConnectionFailure(true)
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .followRedirects(true)
                .followSslRedirects(true)
                .addInterceptor(NetworkConnectionInterceptor(applicationContext))
                .addInterceptor(APIKeyRequestInterceptor)
                .build()
            this.okHttpClient = okHttpClient
            return okHttpClient
        }
}
