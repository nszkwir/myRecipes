package com.spitzer.data.remote.di

import android.content.Context
import com.spitzer.data.BuildConfig
import com.spitzer.data.di.AppDispatchers
import com.spitzer.data.di.Dispatcher
import com.spitzer.data.remote.api.recipe.RecipeAPIService
import com.spitzer.data.remote.api.recipe.RecipeService
import com.spitzer.data.remote.api.recipe.RecipeServiceImpl
import com.spitzer.data.remote.factory.HTTPClientFactory
import com.spitzer.data.remote.factory.HTTPClientFactoryImpl
import com.spitzer.data.remote.factory.ServiceFactory
import com.spitzer.data.remote.factory.ServiceFactoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkConfigurationModule {
    @Provides
    @Singleton
    fun provideHttpClientFactory(
        @ApplicationContext context: Context
    ): HTTPClientFactory {
        return HTTPClientFactoryImpl(context)
    }

    @Provides
    @Singleton
    fun providesServiceFactory(
        clientFactory: HTTPClientFactory
    ): ServiceFactory {
        val baseUrl = BuildConfig.BASE_URL
        return ServiceFactoryImpl(clientFactory, baseUrl)
    }
}

@Module
@InstallIn(SingletonComponent::class)
class ServicesModule {
    @Provides
    @Singleton
    fun providesRecipeService(
        apiClientFactory: ServiceFactory,
        @Dispatcher(AppDispatchers.IO) ioDispatcher: CoroutineDispatcher
    ): RecipeService {
        val apiService =
            apiClientFactory.provideRetrofitClient().create(RecipeAPIService::class.java)
        return RecipeServiceImpl(apiService = apiService, ioDispatcher = ioDispatcher)
    }
}
