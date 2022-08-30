package com.joshowen.forexexchangerates

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

interface DispatchersProvider {
    val main : CoroutineDispatcher
    val io : CoroutineDispatcher
    val default : CoroutineDispatcher
}

@Singleton
class DefaultDispatchers @Inject constructor(): DispatchersProvider {
    override val main: CoroutineDispatcher
    get() = Dispatchers.Main
    override val io: CoroutineDispatcher
    get() = Dispatchers.IO
    override val default: CoroutineDispatcher
    get() = Dispatchers.Default
}