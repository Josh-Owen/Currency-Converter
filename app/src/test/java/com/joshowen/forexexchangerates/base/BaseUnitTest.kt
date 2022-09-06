package com.joshowen.forexexchangerates.base

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.joshowen.forexexchangerates.dispatchers.TestDispatchers
import com.joshowen.forexexchangerates.rules.MainCoroutineScopeRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule

open class BaseUnitTest {

    var testDispatchers : TestDispatchers = TestDispatchers()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineTestRule = MainCoroutineScopeRule(testDispatchers.testDispatcher)


}