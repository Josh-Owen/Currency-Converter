package com.joshowen.forexexchangerates.base

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.joshowen.forexexchangerates.scope.MainCoroutineScopeRule
import com.joshowen.forexexchangerates.scope.TestCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Rule

open class BaseUnitTest {

    @get:Rule
    val coroutineTestRule = MainCoroutineScopeRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

}