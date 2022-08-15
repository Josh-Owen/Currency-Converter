package com.joshowen.forexexchangerates.base.tests.ui

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.adevinta.android.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.joshowen.forexexchangerates.R
import com.joshowen.forexexchangerates.base.base.BaseUITest
import com.joshowen.forexexchangerates.base.utils.nthChildOf
import org.hamcrest.CoreMatchers
import org.junit.Test

class CurrencyHistoryFragmentTest : BaseUITest() {

    @Test
    fun displayScreenTitle() {
        assertDisplayed(R.string.currency_history_page_title)
    }

    // Navigation
    fun navigateToCurrencyPage() {
        Espresso.onView(
            CoreMatchers.allOf(
                withId(R.id.tvCurrencyName),
                ViewMatchers.isDescendantOfA(nthChildOf(withId(R.id.rvExchangeRates), 0))
            )
        ).check(ViewAssertions.matches(ViewMatchers.withText("USD")))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed())).perform(ViewActions.click())

        assertDisplayed(R.string.currency_history_page_title)
    }
    //endregion

}