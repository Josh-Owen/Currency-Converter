package com.joshowen.forexexchangerates.base

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.joshowen.forexexchangerates.TestDispatchers
import com.joshowen.forexexchangerates.scope.MainCoroutineScopeRule
import com.joshowen.forexexchangerates.scope.TestCoroutineRule

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.GlobalScope.coroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Rule
import kotlin.time.Duration.Companion.milliseconds

open class BaseUnitTest {

    var testDispatchers : TestDispatchers = TestDispatchers()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineTestRule = MainCoroutineScopeRule(testDispatchers.testDispatcher)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()



    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun <T> Flow<T>.testWithScheduler(
        validate: suspend ReceiveTurbine<T>.() -> Unit
    ) {
        val testScheduler = coroutineContext[TestCoroutineScheduler]
        return if (testScheduler == null) {
            test(validate)
        } else {
            flowOn(UnconfinedTestDispatcher(testScheduler))
                .test(validate)
        }
    }

}