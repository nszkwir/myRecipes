package com.spitzer.data.remote.di

import com.spitzer.data.di.AppDispatchers
import com.spitzer.data.di.Dispatcher
import com.spitzer.data.remote.api.recipe.RecipeAPIService
import com.spitzer.data.remote.api.recipe.RecipeService
import com.spitzer.data.remote.api.recipe.RecipeServiceImpl
import com.spitzer.data.remote.factory.ServiceFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RemoteModule {

    @Provides
    @Singleton
    fun providesRecipeService(
        apiClientFactory: ServiceFactory,
        @Dispatcher(AppDispatchers.IO) ioDispatcher: CoroutineDispatcher
    ): RecipeService {
        val apiService = apiClientFactory.client.create(RecipeAPIService::class.java)
        return RecipeServiceImpl(apiService = apiService, ioDispatcher = ioDispatcher)
    }
}
