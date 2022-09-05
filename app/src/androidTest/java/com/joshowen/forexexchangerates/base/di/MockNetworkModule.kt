package com.joshowen.forexexchangerates.base.di

import com.joshowen.repository.di.FXExchangeModule
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import mockwebserver3.MockWebServer
import okhttp3.HttpUrl

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [FXExchangeModule::class]
)
class MockNetworkModule : FXExchangeModule() {

    override fun baseUrl(): HttpUrl {
        return MockWebServer().url("http://localhost:8080/")
    }

}