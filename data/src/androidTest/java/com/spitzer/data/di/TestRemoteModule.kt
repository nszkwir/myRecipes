package com.spitzer.data.di


import com.spitzer.data.factory.TestHttpClientFactory
import com.spitzer.data.factory.TestServiceFactory
import com.spitzer.data.utils.TestUrlConfiguration
import com.spitzer.data.remote.di.NetworkConfigurationModule
import com.spitzer.data.remote.factory.HTTPClientFactory
import com.spitzer.data.remote.factory.ServiceFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [NetworkConfigurationModule::class]
)
class TestRemoteModule {
    @Provides
    @Singleton
    fun provideHttpClientFactory(
    ): HTTPClientFactory {
        return TestHttpClientFactory()
    }

    @Provides
    @Singleton
    fun providesServiceFactory(): ServiceFactory {
        val baseUrl = TestUrlConfiguration.BASE_URL
        return TestServiceFactory(TestHttpClientFactory(), baseUrl)
    }
}
