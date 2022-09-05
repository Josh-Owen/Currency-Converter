package com.joshowen.forexexchangerates.base.tests.ui

import android.view.View
import android.widget.ProgressBar
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import com.adevinta.android.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.adevinta.android.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed
import com.joshowen.forexexchangerates.R
import com.joshowen.forexexchangerates.base.DEFAULT_APPLICATION_CONVERSION_AMOUNT
import com.joshowen.forexexchangerates.base.DEFAULT_APP_CURRENCY
import com.joshowen.forexexchangerates.base.base.BaseUITest
import com.joshowen.forexexchangerates.base.idleresources.ViewVisibilityIdlingResource
import com.joshowen.forexexchangerates.base.utils.nthChildOf
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.core.AllOf.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain

@HiltAndroidTest
@LargeTest
class CurrencyHistoryFragmentTest : BaseUITest() {


    //region Variables & Class Members

    @get:Rule
    var hiltRule: RuleChain = RuleChain.outerRule(HiltAndroidRule(this)).around(mActivityRule)

    //endregion

    //region Tests


    @Test
    fun doesDisplayScreenTitle() {
        navigateToCurrencyDetails()
        assertDisplayed(R.string.currency_history_page_title)
    }

    @Test
    fun doesDisplayUserSpecifiedCurrencyAmount() {
        navigateToCurrencyDetails()
        assertDisplayed("${DEFAULT_APP_CURRENCY.currencyCode} $DEFAULT_APPLICATION_CONVERSION_AMOUNT")
    }

    @Test
    fun doesProgressBarDisappearOnceStateHasBeenPropagated() {
        navigateToCurrencyDetails()
        mActivityRule.scenario.onActivity {
            IdlingRegistry.getInstance().register(
                ViewVisibilityIdlingResource(
                    it.findViewById<ProgressBar>(R.id.pbLoadingPriceHistory),
                    View.GONE
                )
            )
        }
        assertNotDisplayed(R.id.pbLoadingPriceHistory)
    }

    @Test
    fun doesBackArrowNavigateBackToHomeScreen() {
        navigateToCurrencyDetails()
        Espresso.pressBack()
        assertDisplayed(R.string.list_of_currencies_page_title)
    }

    @Test
    fun doesDisplayPriceHistory() {
        navigateToCurrencyDetails()
        mActivityRule.scenario.onActivity {
            IdlingRegistry.getInstance().register(
                ViewVisibilityIdlingResource(
                    it.findViewById<ProgressBar>(R.id.pbLoadingPriceHistory),
                    View.GONE
                )
            )
        }

        onView(
            allOf(
                withId(R.id.tvFirstCurrencyHeader),
                isDescendantOfA(nthChildOf(withId(R.id.rvHistoricPrices), 0))
            )
        ).check(matches(withText(R.string.currency_codes_usd)))
            .check(matches(isDisplayed()))

        onView(
            allOf(
                withId(R.id.tvDate),
                isDescendantOfA(nthChildOf(withId(R.id.rvHistoricPrices), 1))
            )
        ).check(matches(withText("2022-08-08")))
            .check(matches(isDisplayed()))

        onView(
            allOf(
                withId(R.id.tvDate),
                isDescendantOfA(nthChildOf(withId(R.id.rvHistoricPrices), 2))
            )
        ).check(matches(withText("2022-08-09")))
            .check(matches(isDisplayed()))

        onView(
            allOf(
                withId(R.id.tvDate),
                isDescendantOfA(nthChildOf(withId(R.id.rvHistoricPrices), 3))
            )
        ).check(matches(withText("2022-08-10")))
            .check(matches(isDisplayed()))

        onView(
            allOf(
                withId(R.id.tvDate),
                isDescendantOfA(nthChildOf(withId(R.id.rvHistoricPrices), 4))
            )
        ).check(matches(withText("2022-08-11")))
            .check(matches(isDisplayed()))

        onView(
            allOf(
                withId(R.id.tvDate),
                isDescendantOfA(nthChildOf(withId(R.id.rvHistoricPrices), 5))
            )
        ).check(matches(withText("2022-08-12")))
            .check(matches(isDisplayed()))

        onView(
            allOf(
                withId(R.id.tvDate),
                isDescendantOfA(nthChildOf(withId(R.id.rvHistoricPrices), 6))
            )
        ).check(matches(withText("2022-08-13")))
            .check(matches(isDisplayed()))

    }


    //endregion

    //region Navigation


    private fun navigateToCurrencyDetails() {
        mActivityRule.scenario.onActivity {
            IdlingRegistry.getInstance().register(
                ViewVisibilityIdlingResource(
                    it.findViewById<ProgressBar>(R.id.pbLoadCurrency),
                    View.GONE
                )
            )
        }

        onView(
            allOf(
                withId(R.id.tvCurrencyName),
                isDescendantOfA(nthChildOf(withId(R.id.rvExchangeRates), 0))
            )
        ).perform(ViewActions.click())
    }
    //endregion

}