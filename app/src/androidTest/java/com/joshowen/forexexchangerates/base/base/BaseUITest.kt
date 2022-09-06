package com.joshowen.forexexchangerates.base.base

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.joshowen.forexexchangerates.base.dispatchers.SuccessDispatcher
import com.joshowen.forexexchangerates.ui.MainActivity
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
abstract class BaseUITest {

    val mActivityRule =
        ActivityScenarioRule(MainActivity::class.java)

    val mockWebServer = MockWebServer()

    @Before
    fun setup() {
        mockWebServer.dispatcher = SuccessDispatcher()
        mockWebServer.start(8080)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }
}