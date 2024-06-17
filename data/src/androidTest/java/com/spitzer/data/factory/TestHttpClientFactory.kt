package com.spitzer.data.factory

import com.spitzer.data.remote.factory.HTTPClientFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class TestHttpClientFactory(
) : HTTPClientFactory{
    override fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    setLevel(HttpLoggingInterceptor.Level.BODY)
                }
            ).build()
    }
}
