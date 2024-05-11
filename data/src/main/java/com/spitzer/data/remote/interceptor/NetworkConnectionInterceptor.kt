package com.spitzer.data.remote.interceptor

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.spitzer.data.BuildConfig
import com.spitzer.entity.network.NetworkError
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response

internal class NetworkConnectionInterceptor(
    @ApplicationContext private val context: Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (hasInternetConnection()) {
            val response = chain.proceed(chain.request())
            if (BuildConfig.DEBUG) {
                if (response.code() == 999) {
                    throw NetworkError.NoInternet
                }
            }
            return response
        } else {
            throw NetworkError.NoInternet
        }
    }

    @SuppressLint("MissingPermission")
    private fun hasInternetConnection(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            ?: false
    }
}
