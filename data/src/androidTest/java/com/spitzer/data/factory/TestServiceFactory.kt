package com.spitzer.data.factory

import com.spitzer.data.remote.factory.HTTPClientFactory
import com.spitzer.data.remote.factory.MoshiFactory
import com.spitzer.data.remote.factory.ServiceFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class TestServiceFactory(
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
