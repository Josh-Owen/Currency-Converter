package com.joshowen.repository.base

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.joshowen.repository.scope.MainCoroutineScopeRule
import org.junit.Rule

open class BaseUnitTest {

    @get:Rule
    val coroutineTestRule = MainCoroutineScopeRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

}