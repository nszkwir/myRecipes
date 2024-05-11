package com.spitzer.data.remote.interceptor

import com.spitzer.data.BuildConfig
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

internal object APIKeyRequestInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()
        request = request.newBuilder()
            .header("x-api-key", BuildConfig.RECIPES_API_KEY)
            .build()
        return chain.proceed(request)
    }
}