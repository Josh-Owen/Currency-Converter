package com.joshowen.forexexchangerates.base.base

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.jakewharton.espresso.OkHttp3IdlingResource
import com.joshowen.forexexchangerates.ui.MainActivity
import com.joshowen.forexexchangerates.base.ViewVisibilityIdlingResource
import dagger.hilt.android.testing.HiltAndroidTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
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

//    val mActivityRule = ActivityScenarioRule(MainActivity::class.java)
//        @Rule get
//
    var activityTestRule = ActivityTestRule(MainActivity::class.java, true, false)
        @Rule get

    private var progressBarGoneIdlingResource: ViewVisibilityIdlingResource? = null

     val mockWebServer = MockWebServer()

     private lateinit var okHttp3IdlingResource: OkHttp3IdlingResource


    var url = "http://127.0.0.1:8080"

    @Before
    fun setup() {
        okHttp3IdlingResource = OkHttp3IdlingResource.create(
            "okhttp",
            OkHttpClient()
        )

        IdlingRegistry.getInstance().register(
            okHttp3IdlingResource
        )
        mockWebServer.url(url)
        mockWebServer.start(8080)

        IdlingRegistry.getInstance().register(okHttp3IdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(okHttp3IdlingResource)
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(progressBarGoneIdlingResource)
       // mActivityRule.scenario.close()
    }

    fun nthChildOf(parentMatcher: Matcher<View>, childPosition: Int): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("position $childPosition of parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                if (view.parent !is ViewGroup) return false
                val parent = view.parent as ViewGroup

                return (parentMatcher.matches(parent)
                        && parent.childCount > childPosition
                        && parent.getChildAt(childPosition) == view)
            }
        }
    }
}