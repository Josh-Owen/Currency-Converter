package com.joshowen.forexexchangerates.base.tests.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.adevinta.android.barista.assertion.BaristaVisibilityAssertions.assertContains
import com.adevinta.android.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.joshowen.forexexchangerates.R
import com.joshowen.forexexchangerates.base.DEFAULT_APP_CURRENCY
import com.joshowen.forexexchangerates.base.base.BaseUITest
import org.junit.Test

class CurrencyListFragmentTest : BaseUITest() {

    //region Variables & Class Members


    //endregion

    //region Tests

    @Test
    fun displayScreenTitle() {
        assertDisplayed(R.string.list_of_currencies_page_title)
    }

    @Test
    fun isCurrencyFieldDisplayed() {
        onView(withId(R.id.etAmount))
            .check(matches(isDisplayed()))
    }

    @Test
    fun insertValidCurrencyFormatIntoCurrencyField() {
        onView(withId(R.id.etAmount))
            .perform(click(), clearText(), typeText("100.00"))
            .check(matches(withText("100.00")))
    }

    @Test
    fun doesCurrencyFieldAllowForDeletion() {
        onView(withId(R.id.etAmount))
            .perform(click(), clearText())
            .check(matches(withText("")))
    }

    @Test
    fun insertInvalidCharactersIntoCurrencyField() {
        onView(withId(R.id.etAmount))
            .perform(click(), clearText(), typeText("Hello"))
            .check(matches(withText("")))
    }

    @Test
    fun displaysDefaultApplicationCurrencyText() {
        assertContains(R.id.tvDefaultCurrencyTitle, DEFAULT_APP_CURRENCY.currencyCode)
    }

    @Test
    fun isProgressBarVisiblePriorToDataBeenReturned() {
        assertDisplayed(R.id.pbLoadCurrency)
    }

    @Test
    fun doesProgressBarDisappearAfterDataHasBeenReturned() {
//        var progressBarGoneIdlingResource: ViewVisibilityIdlingResource? = null
//        mActivityRule.scenario.onActivity {
//            progressBarGoneIdlingResource = ViewVisibilityIdlingResource(
//                it.findViewById<ProgressBar>(R.id.pbLoadCurrency),
//                View.GONE
//            )
//        }
//
//        IdlingRegistry.getInstance().register(progressBarGoneIdlingResource)
//        assertNotDisplayed(R.id.pbLoadCurrency)
    }

    @Test
    fun hideProgressBarAndDisplaysToast() {
//        var progressBarGoneIdlingResource: ViewVisibilityIdlingResource? = null
//        mActivityRule.scenario.onActivity {
//            progressBarGoneIdlingResource = ViewVisibilityIdlingResource(
//                it.findViewById<ProgressBar>(R.id.pbLoadCurrency),
//                View.GONE
//            )
//        }
//        IdlingRegistry.getInstance().register(progressBarGoneIdlingResource)
//
//        assertDisplayed(R.string.generic_network_error)
    }

    @Test
    fun displayCompleteListOfCurrencyExchanges() {
//        var progressBarGoneIdlingResource: ViewVisibilityIdlingResource? = null
//        mActivityRule.scenario.onActivity {
//            progressBarGoneIdlingResource = ViewVisibilityIdlingResource(
//                it.findViewById<RecyclerView>(R.id.rvExchangeRates),
//                View.GONE
//            )
//        }
//        IdlingRegistry.getInstance().register(progressBarGoneIdlingResource)
//
//        assertRecyclerViewItemCount(R.id.rvExchangeRates, 10)
//
//        onView(
//            allOf(
//                withId(R.id.tvCurrencyName),
//                isDescendantOfA(nthChildOf(withId(R.id.rvExchangeRates), 0))
//            )
//        ).check(matches(withText("USD")))
//            .check(matches(isDisplayed()))
//
//
//        onView(
//            allOf(
//                withId(R.id.tvCurrencyName),
//                isDescendantOfA(nthChildOf(withId(R.id.rvExchangeRates), 9))
//            )
//        ).check(matches(withText("NZD")))
//            .check(matches(isDisplayed()))

    }

    @Test
    fun navigateToCurrencyDetails() {
//
//        onView(
//            allOf(
//                withId(R.id.tvCurrencyName),
//                isDescendantOfA(nthChildOf(withId(R.id.rvExchangeRates), 0))
//            )
//        ).check(matches(withText("USD")))
//            .check(matches(isDisplayed())).perform(click())
//
//        assertDisplayed(R.string.currency_history_page_title)
    }

    @Test
    fun doesLongPressRecyclerViewElementSelectIt() {

    }

    @Test
    fun doesLongPressingMultipleRecyclerViewElementsSelect() {

    }

    @Test
    fun doesSelectingCurrenciesUpdateActionBarTitle() {

    }


    @Test
    fun doesSelecting1CurrencyAndPressingActionButtonNavigateToDetails() {

    }

    @Test
    fun doesSelecting2CurrenciesAndPressingActionButtonNavigateToDetails() {

    }

    @Test
    fun doesSelecting3CurrenciesAndPressingActionButtonNavigateToDetails() {

    }

    @Test
    fun doesUpdatingSpecifiedCurrencyUpdateConversionValues() {

    }

    @Test
    fun testSuccessfulResponse() {
//
//        val mockWebServer = MockWebServer()
//        mockWebServer.start()
//        val url = mockWebServer.url("/latest").toString()
//        mockWebServer.enqueue(MockResponse().setBody(FileReader.readStringFromFile("network_files/FetchCurrentPriceResponse.json")))
//        assertDisplayed(R.string.list_of_currencies_page_title)
//        Thread.sleep(2000)
    }

    //endregion
}