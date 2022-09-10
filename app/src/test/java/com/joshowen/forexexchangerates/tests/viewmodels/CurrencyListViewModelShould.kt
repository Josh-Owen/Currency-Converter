package com.joshowen.forexexchangerates.tests.viewmodels

import android.app.Application
import app.cash.turbine.test
import com.joshowen.forexexchangerates.base.BaseUnitTest
import com.joshowen.forexexchangerates.data.Currency
import com.joshowen.forexexchangerates.data.CurrencyType
import com.joshowen.forexexchangerates.repositories.fxexchange.ForeignExchangeRepositoryImpl
import com.joshowen.forexexchangerates.retrofit.wrappers.ApiException
import com.joshowen.forexexchangerates.retrofit.wrappers.ApiSuccess
import com.joshowen.forexexchangerates.ui.currencylist.CurrencyListFragmentVM
import com.joshowen.forexexchangerates.ui.currencylist.CurrencyListPageState
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class CurrencyListViewModelShould : BaseUnitTest() {

    //region Variables & Class Members
    private val repository: ForeignExchangeRepositoryImpl = mock()

    private var originalCurrencyValues: List<Currency> = listOf(
        Currency(CurrencyType.EUROS, 0.0),
        Currency(CurrencyType.US_DOLLARS, 1.0),
        Currency(CurrencyType.JAPANESE_YEN, 2.0),
        Currency(CurrencyType.GREAT_BRITISH_POUNDS, 3.0),
        Currency(CurrencyType.AUSTRALIAN_DOLLARS, 4.0),
        Currency(CurrencyType.CANADIAN_DOLLARS, 5.0),
        Currency(CurrencyType.SWISS_FRANC, 6.0),
        Currency(CurrencyType.CHINESE_YUAN, 7.0),
        Currency(CurrencyType.SWEDISH_KRONA, 8.0),
        Currency(CurrencyType.NEW_ZEALAND_DOLLARS, 9.0)
    )

    private var transformedCurrencyValues: List<Currency> = listOf(
        Currency(CurrencyType.EUROS, 0.0),
        Currency(CurrencyType.US_DOLLARS, 100.0),
        Currency(CurrencyType.JAPANESE_YEN, 200.0),
        Currency(CurrencyType.GREAT_BRITISH_POUNDS, 300.0),
        Currency(CurrencyType.AUSTRALIAN_DOLLARS, 400.0),
        Currency(CurrencyType.CANADIAN_DOLLARS, 500.0),
        Currency(CurrencyType.SWISS_FRANC, 600.0),
        Currency(CurrencyType.CHINESE_YUAN, 700.0),
        Currency(CurrencyType.SWEDISH_KRONA, 800.0),
        Currency(CurrencyType.NEW_ZEALAND_DOLLARS, 900.0)
    )

    private var updatedCurrencyValues: List<Currency> = listOf(
        Currency(CurrencyType.EUROS, 0.0),
        Currency(CurrencyType.US_DOLLARS, 1000.0),
        Currency(CurrencyType.JAPANESE_YEN, 2000.0),
        Currency(CurrencyType.GREAT_BRITISH_POUNDS, 3000.0),
        Currency(CurrencyType.AUSTRALIAN_DOLLARS, 4000.0),
        Currency(CurrencyType.CANADIAN_DOLLARS, 5000.0),
        Currency(CurrencyType.SWISS_FRANC, 6000.0),
        Currency(CurrencyType.CHINESE_YUAN, 7000.0),
        Currency(CurrencyType.SWEDISH_KRONA, 8000.0),
        Currency(CurrencyType.NEW_ZEALAND_DOLLARS, 9000.0)
    )

    private var application: Application = mock()

    private val supportedCurrencies = listOf(
        CurrencyType.EUROS.currencyCode,
        CurrencyType.US_DOLLARS.currencyCode,
        CurrencyType.JAPANESE_YEN.currencyCode,
        CurrencyType.GREAT_BRITISH_POUNDS.currencyCode,
        CurrencyType.AUSTRALIAN_DOLLARS.currencyCode,
        CurrencyType.CANADIAN_DOLLARS.currencyCode,
        CurrencyType.SWISS_FRANC.currencyCode,
        CurrencyType.CHINESE_YUAN.currencyCode,
        CurrencyType.SWEDISH_KRONA.currencyCode,
        CurrencyType.NEW_ZEALAND_DOLLARS.currencyCode
    ).joinToString()

    private val genericRuntimeException = RuntimeException("Something went wrong.")

    private val defaultCurrency = CurrencyType.EUROS

    private val defaultCurrencyValue = 100
    private val updatedCurrencyValue = 1000

    //endregion


    //region Tests

    @Test
    fun doesEmitDefaultApplicationCurrency() = runBlocking(testDispatchers.main) {
        val viewModel = mockSuccessfulCase()
        viewModel.outputs.fetchDefaultApplicationCurrencyFlow().test {
            val emission = awaitItem()
            assertEquals(defaultCurrency.currencyCode, emission)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun doesUpdatingCurrencyFieldUpdateViewModelState() = runBlocking(testDispatchers.io) {
        val viewModel = mockSuccessfulCase()
        val priorToUpdatingAmount = viewModel.outputs.fetchSpecifiedAmountOfCurrency()
        assertEquals(defaultCurrencyValue, priorToUpdatingAmount)
        viewModel.inputs.setCurrencyAmount(200)
        val updatedAmount = viewModel.fetchSpecifiedAmountOfCurrency()
        assertEquals(updatedAmount, 200)
    }

    @Test
    fun isListLoadingStatePropagated() = runBlocking(testDispatchers.io) {
        val viewModel = mockSuccessfulCase()
        viewModel.fetchCurrencyInformation()
        viewModel.outputs.fetchUIStateFlow().test {
            assertTrue(awaitItem() is CurrencyListPageState.Loading)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun isListSuccessfulStatePropagated() = runBlocking(testDispatchers.io) {
        val viewModel = mockSuccessfulCase()

        viewModel.inputs.setCurrencyAmount(defaultCurrencyValue)
        viewModel.inputs.fetchCurrencyInformation()

        viewModel.outputs.fetchUIStateFlow().test {
            awaitItem() // Ignore our initial state Idle state
            val emittedValue = awaitItem()
            assertTrue(emittedValue is CurrencyListPageState.Success && emittedValue.data == transformedCurrencyValues)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun isListErrorStatePropagated() = runBlocking(testDispatchers.io) {
        val viewModel = mockErrorCase()

        viewModel.fetchCurrencyInformation()

        viewModel.outputs.fetchUIStateFlow().test {
            assertTrue(awaitItem() is CurrencyListPageState.Error)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun doesUpdatingSpecifiedCurrencyUpdateCurrencyPrices() = runBlocking(testDispatchers.io) {
        val viewModel = mockSuccessfulCase()

        viewModel.inputs.setCurrencyAmount(updatedCurrencyValue)

        viewModel.inputs.fetchCurrencyInformation()

        viewModel.outputs.fetchUIStateFlow().test {
            awaitItem()
            val emittedValue = awaitItem()
            assertTrue(emittedValue is CurrencyListPageState.Success && emittedValue.data == updatedCurrencyValues)
            cancelAndConsumeRemainingEvents()
        }
    }

    //endregion

    // region Test Cases
    private suspend fun mockErrorCase(): CurrencyListFragmentVM {
        val viewModel = CurrencyListFragmentVM(application, testDispatchers, repository)
        whenever(
            repository.getCurrencyInformation(defaultCurrency, supportedCurrencies)
        ).thenReturn(flow {
            emit(ApiException(genericRuntimeException))
        })
        return viewModel
    }


    private suspend fun mockSuccessfulCase(): CurrencyListFragmentVM {
        val viewModel = CurrencyListFragmentVM(application, testDispatchers, repository)

        whenever(
            repository.getCurrencyInformation(
                defaultCurrency,
                supportedCurrencies
            )
        ).thenReturn(
            flow {
                emit(ApiSuccess(originalCurrencyValues))
            })
        return viewModel
    }
    //endregion
}
