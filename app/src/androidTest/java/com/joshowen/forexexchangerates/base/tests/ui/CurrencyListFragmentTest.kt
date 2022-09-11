package com.joshowen.forexexchangerates.base.tests.ui

import android.view.View
import android.widget.ProgressBar
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import com.adevinta.android.barista.assertion.BaristaRecyclerViewAssertions.assertRecyclerViewItemCount
import com.adevinta.android.barista.assertion.BaristaVisibilityAssertions.assertContains
import com.adevinta.android.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.adevinta.android.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed
import com.joshowen.forexexchangerates.R
import com.joshowen.forexexchangerates.base.DEFAULT_APP_CURRENCY
import com.joshowen.forexexchangerates.base.base.BaseUITest
import com.joshowen.forexexchangerates.base.dispatchers.ErrorDispatcher
import com.joshowen.forexexchangerates.base.dispatchers.SuccessDispatcher
import com.joshowen.forexexchangerates.base.idleresources.ViewVisibilityIdlingResource
import com.joshowen.forexexchangerates.base.utils.views.nthChildOf
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.core.AllOf.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain

@HiltAndroidTest
@LargeTest
class CurrencyListFragmentTest : BaseUITest() {


    //region Variables & Class Members

    @get:Rule
    var hiltRule: RuleChain = RuleChain.outerRule(HiltAndroidRule(this)).around(mActivityRule)

    //endregion

    //region Tests

    @Test
    fun doesDisplayScreenTitle() {
        assertDisplayed(R.string.list_of_currencies_page_title)
    }

    @Test
    fun isCurrencyFieldDisplayed() {
        onView(withId(R.id.etAmount))
            .check(matches(isDisplayed()))
    }

    @Test
    fun canInsertValidCurrencyFormatIntoCurrencyField() {
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
    fun canInsertInvalidCharactersIntoCurrencyField() {
        onView(withId(R.id.etAmount))
            .perform(click(), clearText(), typeText("Hello"))
            .check(matches(withText("")))
    }

    @Test
    fun doesDisplaysDefaultApplicationCurrencyText() {
        assertContains(R.id.tvDefaultCurrencyTitle, DEFAULT_APP_CURRENCY.currencyCode)
    }

    @Test
    fun doesProgressBarDisappearOnceStateHasBeenPropagated() {
        mActivityRule.scenario.onActivity {
            IdlingRegistry.getInstance().register(
                ViewVisibilityIdlingResource(
                    it.findViewById<ProgressBar>(R.id.pbLoadCurrency),
                    View.GONE
                )
            )
        }
        assertNotDisplayed(R.id.pbLoadCurrency)
    }

    @Test
    fun doesDisplaySnackBarOnNetworkError() {
        mockWebServer.dispatcher = ErrorDispatcher()

        mActivityRule.scenario.onActivity {
            IdlingRegistry.getInstance().register(
                ViewVisibilityIdlingResource(
                    it.findViewById<ProgressBar>(R.id.pbLoadCurrency),
                    View.GONE
                )
            )
        }

        onView(withText(R.string.generic_network_error))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun displaysRetryButtonOnError() {
        mockWebServer.dispatcher = ErrorDispatcher()
        mActivityRule.scenario.onActivity {
            IdlingRegistry.getInstance().register(
                ViewVisibilityIdlingResource(
                    it.findViewById<ProgressBar>(R.id.pbLoadCurrency),
                    View.GONE
                )
            )
        }
        assertDisplayed(R.id.btnRetry)
    }

    @Test
    fun doesDisplayListOfCurrencies() {
        mockWebServer.dispatcher = SuccessDispatcher()
        mActivityRule.scenario.onActivity {
            IdlingRegistry.getInstance().register(
                ViewVisibilityIdlingResource(
                    it.findViewById<ProgressBar>(R.id.pbLoadCurrency),
                    View.GONE
                )
            )
        }

        assertRecyclerViewItemCount(R.id.rvExchangeRates, 10)

        onView(
            allOf(
                withId(R.id.tvCurrencyName),
                isDescendantOfA(nthChildOf(withId(R.id.rvExchangeRates), 0))
            )
        ).check(matches(withText("USD")))
            .check(matches(isDisplayed()))

        onView(
            allOf(
                withId(R.id.tvCurrencyName),
                isDescendantOfA(nthChildOf(withId(R.id.rvExchangeRates), 9))
            )
        ).check(matches(withText("NZD")))
            .check(matches(isDisplayed()))
    }

    @Test
    fun doesSelectingAndDeselectingCurrenciesPerformAction() {
        // Blocked by bug in SelectionTracker library: https://github.com/android/android-test/issues/1382
    }

    @Test
    fun doesSelectingCurrenciesUpdateActionBarTitle() {
        // Blocked by bug in SelectionTracker library: https://github.com/android/android-test/issues/1382
    }

    @Test
    fun doesSelecting1CurrencyAndPressingActionButtonNavigateToPriceHistory() {
        // Blocked by bug in SelectionTracker library: https://github.com/android/android-test/issues/1382

//        mActivityRule.scenario.onActivity {
//            IdlingRegistry.getInstance().register(ViewVisibilityIdlingResource(
//                it.findViewById<ProgressBar>(R.id.pbLoadCurrency),
//                View.GONE
//            ))
//        }
//
//        onView(
//            allOf(
//                withId(R.id.tvCurrencyName),
//                isDescendantOfA(nthChildOf(withId(R.id.rvExchangeRates), 0))
//            )
//        ).perform(longClick())
//
//        onView(
//            allOf(
//                withId(R.id.tvCurrencyName),
//                isDescendantOfA(nthChildOf(withId(R.id.rvExchangeRates), 1))
//            )
//        ).perform(longClick())
//
//        onView(
//            allOf(
//                withId(R.id.tvCurrencyName),
//                isDescendantOfA(nthChildOf(withId(R.id.rvExchangeRates), 2))
//            )
//        ).perform(longClick())
    }

    @Test
    fun doesSelecting2CurrenciesAndPressingActionButtonNavigateToPriceHistory() {
        // Blocked by bug in SelectionTracker library: https://github.com/android/android-test/issues/1382
    }

    @Test
    fun doesSelecting3CurrenciesAndPressingActionButtonNavigateToPriceHistory() {
        // Blocked by bug in SelectionTracker library: https://github.com/android/android-test/issues/1382
    }

    @Test
    fun doesUpdatingSpecifiedCurrencyUpdateConversionValues() {
        // Blocked by bug in SelectionTracker library: https://github.com/android/android-test/issues/1382
    }

    @Test
    fun doesCurrencyAmountGetDisabledUponSelection() {
        // Blocked by bug in SelectionTracker library: https://github.com/android/android-test/issues/1382
    }

    //endregion
}