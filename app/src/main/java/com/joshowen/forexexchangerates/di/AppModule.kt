package com.joshowen.forexexchangerates.di

import com.joshowen.forexexchangerates.dispatchers.DefaultDispatchers
import com.joshowen.forexexchangerates.dispatchers.DispatchersProvider
import com.joshowen.forexexchangerates.repositories.fxexchange.ForeignExchangeRepository
import com.joshowen.forexexchangerates.repositories.fxexchange.ForeignExchangeRepositoryImpl

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
internal interface AppModule {

    @Binds
    @ViewModelScoped
    fun getDispatchers(dispatcher: DefaultDispatchers): DispatchersProvider

    @Binds
    @ViewModelScoped
    fun getForeignExchangeRepository(repository: ForeignExchangeRepositoryImpl): ForeignExchangeRepository


}