package com.spitzer.data.remote.factory

import android.content.Context
import com.spitzer.data.remote.interceptor.APIKeyRequestInterceptor
import com.spitzer.data.remote.interceptor.NetworkConnectionInterceptor
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

interface HTTPClientFactory {
    fun provideOkHttpClient(): OkHttpClient
}

class HTTPClientFactoryImpl(
    @ApplicationContext private val applicationContext: Context
) : HTTPClientFactory {
    override fun provideOkHttpClient(): OkHttpClient {
        val timeout: Long = 30
        return OkHttpClient().newBuilder()
            .retryOnConnectionFailure(true)
            .connectTimeout(timeout, TimeUnit.SECONDS)
            .readTimeout(timeout, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .addInterceptor(NetworkConnectionInterceptor(applicationContext))
            .addInterceptor(APIKeyRequestInterceptor)
            .build()
    }
}
