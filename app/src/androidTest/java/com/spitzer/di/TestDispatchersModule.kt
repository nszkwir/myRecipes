package com.spitzer.di

import com.spitzer.data.di.AppDispatchers
import com.spitzer.data.di.Dispatcher
import com.spitzer.data.di.DispatchersModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestDispatcher

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DispatchersModule::class],
)
internal object TestDispatchersModule {
    @Provides
    @Dispatcher(AppDispatchers.IO)
    fun providesIODispatcher(testDispatcher: TestDispatcher): CoroutineDispatcher = testDispatcher

    @Provides
    @Dispatcher(AppDispatchers.Default)
    fun providesDefaultDispatcher(
        testDispatcher: TestDispatcher,
    ): CoroutineDispatcher = testDispatcher
}
