package com.joshowen.forexexchangerates.base.base

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.jakewharton.espresso.OkHttp3IdlingResource
import com.joshowen.forexexchangerates.ui.MainActivity
import com.joshowen.forexexchangerates.base.idleresources.ViewVisibilityIdlingResource
import com.joshowen.forexexchangerates.base.utils.FileReader
import com.joshowen.forexexchangerates.base.utils.FileReader.readStringFromFile
import dagger.hilt.android.testing.HiltAndroidTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.tls.HandshakeCertificates
import okhttp3.tls.HeldCertificate
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import java.net.InetAddress

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
abstract class BaseUITest {

    @JvmField
    @Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java, true, false)




    private var progressBarGoneIdlingResource: ViewVisibilityIdlingResource? = null

     private lateinit var okHttp3IdlingResource: OkHttp3IdlingResource

     val mockWebServer = MockWebServer()


    @Before
    fun setup() {
        okHttp3IdlingResource = OkHttp3IdlingResource.create(
            "okhttp",
            OkHttpClient()
        )

        IdlingRegistry.getInstance().register(okHttp3IdlingResource)

        mockWebServer.start(8001)
        mockWebServer.url("https://api.apilayer.com/fixer/")



    }



    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(okHttp3IdlingResource)
        IdlingRegistry.getInstance().unregister(progressBarGoneIdlingResource)
        mockWebServer.shutdown()
    }
}