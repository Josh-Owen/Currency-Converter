package com.joshowen.forexexchangerates.dispatchers

import com.joshowen.forexexchangerates.dispatchers.DispatchersProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import javax.inject.Singleton

@Singleton
class TestDispatchers : DispatchersProvider {

    @OptIn(ExperimentalCoroutinesApi::class)
     val testDispatcher = UnconfinedTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    override val main: CoroutineDispatcher
        get() = testDispatcher
    @OptIn(ExperimentalCoroutinesApi::class)
    override val io: CoroutineDispatcher
        get() = testDispatcher
    @OptIn(ExperimentalCoroutinesApi::class)
    override val default: CoroutineDispatcher
        get() = testDispatcher
}