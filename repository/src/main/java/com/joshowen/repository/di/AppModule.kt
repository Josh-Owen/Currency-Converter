package com.joshowen.repository.di

import com.joshowen.repository.repository.ForeignExchangeRepositoryImpl
import com.joshowen.repository.repository.ForeignExchangeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
interface AppModule {

    @Binds
    @ViewModelScoped
    fun getForeignExchangeRepository(repository: ForeignExchangeRepositoryImpl): ForeignExchangeRepository

}