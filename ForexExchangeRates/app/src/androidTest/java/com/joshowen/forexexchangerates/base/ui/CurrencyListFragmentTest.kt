package com.joshowen.forexexchangerates.base.ui


import android.view.KeyEvent
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.adevinta.android.barista.assertion.BaristaRecyclerViewAssertions.assertRecyclerViewItemCount
import com.adevinta.android.barista.assertion.BaristaVisibilityAssertions.assertContains
import com.adevinta.android.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.adevinta.android.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed
import com.joshowen.forexexchangerates.base.DEFAULT_APP_CURRENCY
import com.joshowen.forexexchangerates.ui.MainActivity
import com.joshowen.forexexchangerates.base.base.BaseUITest
import com.joshowen.forexexchangerates.R
import com.joshowen.forexexchangerates.base.FileReader
import com.joshowen.forexexchangerates.base.ViewVisibilityIdlingResource
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.CoreMatchers.allOf
import org.junit.Test


class CurrencyListFragmentTest : BaseUITest() {

    private var progressBarGoneIdlingResource: ViewVisibilityIdlingResource? = null

    @Test
    fun testSuccessfulResponse() {



        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return MockResponse()
                    .setResponseCode(200)
                    .setHeader("apiKey", "Bla")
                    .setBody(FileReader.readStringFromFile("network_files/FetchCurrentPriceResponse.json")
                    )
            }
        }

        ActivityScenario.launch(MainActivity::class.java)








    }
//
//    @Test
//    fun testFailedResponse() {
//        mockWebServer.dispatcher = object : Dispatcher() {
//            override fun dispatch(request: RecordedRequest): MockResponse {
//                return MockResponse()
//                    .setResponseCode(200)
//                    .setBody(AssetReaderUtil.getStringFromFile("success_response.json"))
//                    .throttleBody(1024, 5, TimeUnit.SECONDS)
//            }
//        }
//    }

    @Test
    fun doesProgressDisappear() {
//        progressBarGoneIdlingResource =
//            ViewVisibilityIdlingResource(
//                activityTestRule.activity.findViewById(R.id.pbLoadCurrency),
//                View.GONE
//            )
//        IdlingRegistry.getInstance().register(progressBarGoneIdlingResource)
//
//        mockWebServer.dispatcher = SuccessDispatcher()
//
//
//        onView(withId(R.id.rvExchangeRates)).check(matches(isDisplayed()))
//
//        assertNotDisplayed(R.id.pbLoadCurrency)

    }

    @Test
    fun displayScreenTitle() {
        assertDisplayed(R.string.list_of_currencies_page_title)
    }

    @Test
    fun isCurrencyFieldDisplayed() {
        onView(withId(R.id.etAmount))
            .check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun insertValidCurrencyFormatIntoCurrencyField() {
        onView(withId(R.id.etAmount))
            .perform(click())
            .perform(typeText("100.0"))
            .check(matches(ViewMatchers.withText("100.0")))
            .perform(typeText(""))
    }

    @Test
    fun doesCurrencyFieldAllowForDeletion() {
        onView(withId(R.id.etAmount))
            .perform(click())
            .perform(typeText("1"))
            .perform(pressKey(KeyEvent.KEYCODE_DEL))
            .check(matches(ViewMatchers.withText("")))
    }

    @Test
    fun insertInvalidCharactersIntoCurrencyField() {
        onView(withId(R.id.etAmount))
            .perform(click())
            .perform(typeText("Hello"))
            .check(matches(ViewMatchers.withText("")))

    }

    @Test
    fun displaysDefaultApplicationCurrencyText() {
        assertContains(R.id.tvDefaultCurrencyTitle, DEFAULT_APP_CURRENCY.currencyCode)
    }

    @Test
    fun displayProgressBar() {
        assertDisplayed(R.id.pbLoadCurrency)
    }

    @Test
    fun hideProgressBar() {
//        progressBarGoneIdlingResource =
//            ViewVisibilityIdlingResource(
//                activityTestRule.activity.findViewById(R.id.pbLoadCurrency),
//                View.GONE
//            )
        IdlingRegistry.getInstance().register(progressBarGoneIdlingResource)
        assertNotDisplayed(R.id.pbLoadCurrency)
    }

    @Test
    fun displayCompleteListOfCurrencyExchanges() {
        Thread.sleep(2000)
        assertRecyclerViewItemCount(R.id.rvExchangeRates, 10)

        onView(
            allOf(
                withId(R.id.tvCurrencyName),
                ViewMatchers.isDescendantOfA(nthChildOf(withId(R.id.rvExchangeRates), 0))
            )
        ).check(ViewAssertions.matches(ViewMatchers.withText("USD")))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))


        onView(
            allOf(
                withId(R.id.tvCurrencyName),
                ViewMatchers.isDescendantOfA(nthChildOf(withId(R.id.rvExchangeRates), 9))
            )
        ).check(ViewAssertions.matches(ViewMatchers.withText("NZD")))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))




    }

    @Test
    fun navigateToCurrencyDetails() {
        onView(
            allOf(
                withId(R.id.tvCurrencyName),
                ViewMatchers.isDescendantOfA(nthChildOf(withId(R.id.rvExchangeRates), 0))
            )
        ).check(ViewAssertions.matches(ViewMatchers.withText("USD")))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed())).perform(click())

        assertDisplayed(R.string.currency_history_page_title)
    }


}