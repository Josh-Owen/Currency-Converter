package com.joshowen.forexexchangerates.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.joshowen.forexexchangerates.BuildConfig
import com.joshowen.forexexchangerates.retrofit.apis.ForeignExchangeAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
open class FXExchangeModule {

    protected open fun baseUrl() = "https://api.apilayer.com/fixer/".toHttpUrl()

    @Provides
    @Singleton
    @Named("apiKeyInterceptor")
    fun provideAPIKeyInterceptor(): Interceptor {
        return Interceptor {
            it.proceed(
                it.request()
                    .newBuilder()
                    .addHeader("apiKey", BuildConfig.EXCHANGE_RATE_API_KEY)
                    .build()
            )
        }
    }

    @Provides
    @Singleton
    @Named("httpLoggingInterceptor")
    fun provideHttpLoggingInterceptor(): Interceptor {
        return HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG) {
                level = HttpLoggingInterceptor.Level.BODY
            }
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @Named("httpLoggingInterceptor") httpLoggingInterceptor: Interceptor,
        @Named("apiKeyInterceptor") apiKeyInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(apiKeyInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(50, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().serializeNulls().setLenient().create()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        client: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideFXService(retrofit: Retrofit): ForeignExchangeAPI {
        return retrofit.create(ForeignExchangeAPI::class.java)
    }
}
