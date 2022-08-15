package com.joshowen.forexexchangerates.base.base

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jakewharton.espresso.OkHttp3IdlingResource
import com.joshowen.forexexchangerates.ui.MainActivity
import com.joshowen.forexexchangerates.base.idleresources.ViewVisibilityIdlingResource
import dagger.hilt.android.testing.HiltAndroidTest
import okhttp3.OkHttpClient
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
abstract class BaseUITest {

    val mActivityRule = ActivityScenarioRule(MainActivity::class.java)
        @Rule get


    private var progressBarGoneIdlingResource: ViewVisibilityIdlingResource? = null

     private lateinit var okHttp3IdlingResource: OkHttp3IdlingResource


    @Before
    fun setup() {
        okHttp3IdlingResource = OkHttp3IdlingResource.create(
            "okhttp",
            OkHttpClient()
        )

        IdlingRegistry.getInstance().register(
            okHttp3IdlingResource
        )

        IdlingRegistry.getInstance().register(okHttp3IdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(okHttp3IdlingResource)
        IdlingRegistry.getInstance().unregister(progressBarGoneIdlingResource)
    }
}