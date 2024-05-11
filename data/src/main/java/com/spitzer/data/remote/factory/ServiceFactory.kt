package com.spitzer.data.remote.factory

import android.content.Context
import com.spitzer.data.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject

class ServiceFactory @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var retrofit: Retrofit? = null

    val client: Retrofit
        get() {
            retrofit?.let { return it }

            val retrofit = Retrofit.Builder()
                .client(
                    HTTPClientFactory(context).client
                )
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(MoshiFactory.moshi))
                .build()
            this.retrofit = retrofit
            return retrofit
        }
}
