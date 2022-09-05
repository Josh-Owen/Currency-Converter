package com.joshowen.forexexchangerates.base

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.joshowen.forexexchangerates.TestDispatchers
import com.joshowen.forexexchangerates.scope.MainCoroutineScopeRule
import com.joshowen.forexexchangerates.scope.TestCoroutineRule
import kotlinx.coroutines.*

import kotlinx.coroutines.GlobalScope.coroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.util.concurrent.Executors
import kotlin.time.Duration.Companion.milliseconds

open class BaseUnitTest {

    var testDispatchers : TestDispatchers = TestDispatchers()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineTestRule = MainCoroutineScopeRule(testDispatchers.testDispatcher)


}