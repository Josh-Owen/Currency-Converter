package com.joshowen.forexexchangerates.di

import com.joshowen.forexexchangerates.DefaultDispatchers
import com.joshowen.forexexchangerates.DispatchersProvider

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
interface AppModulee {

    @Binds
    @ViewModelScoped
    fun getDispatchers(dispatcher: DefaultDispatchers): DispatchersProvider

}