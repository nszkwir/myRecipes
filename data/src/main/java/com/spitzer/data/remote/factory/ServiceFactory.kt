package com.spitzer.data.remote.factory

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

interface ServiceFactory {
    fun provideRetrofitClient(): Retrofit
}

class ServiceFactoryImpl(
    private val httpClientFactory: HTTPClientFactory,
    private val baseUrl: String
) : ServiceFactory {
    override fun provideRetrofitClient(): Retrofit {
        return Retrofit.Builder()
            .client(httpClientFactory.provideOkHttpClient())
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create(MoshiFactory.moshi))
            .build()
    }
}
