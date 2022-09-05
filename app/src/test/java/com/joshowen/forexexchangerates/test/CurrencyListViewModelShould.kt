package com.joshowen.forexexchangerates.test

import android.app.Application
import app.cash.turbine.test
import com.joshowen.forexexchangerates.base.BaseUnitTest
import com.joshowen.forexexchangerates.ui.currencylist.CurrencyListFragmentVM
import com.joshowen.forexexchangerates.ui.currencylist.CurrencyListPageState
import com.joshowen.repository.data.Currency
import com.joshowen.repository.enums.CurrencyType
import com.joshowen.repository.repository.ForeignExchangeRepositoryImpl
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.lang.RuntimeException

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

    private var transformedCurrencyValues : List<Currency> = listOf(
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

    private var updatedCurrencyValues : List<Currency> = listOf(
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

    var expectedResult = Result.success(originalCurrencyValues)

    var application: Application = mock()

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

    //endregion

    //region Tests

    @Test
    fun doesEmitDefaultApplicationCurrency() = runBlocking(testDispatchers.main) {
        val viewModel = mockSuccessfulCase()
        viewModel.outputs.fetchDefaultApplicationCurrency().test {
            val emission = awaitItem()
            assertEquals(defaultCurrency.currencyCode, emission)
            cancelAndConsumeRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun doesUpdatingCurrencyFieldUpdateViewModelState() = runBlocking(testDispatchers.io) {
        val viewModel = mockSuccessfulCase()
        val priorToUpdatingAmount = viewModel.amountToConvert.value
        assertEquals(defaultCurrencyValue, priorToUpdatingAmount)
        viewModel.inputs.setCurrencyAmount(200)
        val updatedAmount = viewModel.amountToConvert.value
        assertEquals(updatedAmount, 200)
    }

    @Test
    fun isListLoadingStatePropagated() = runBlocking(testDispatchers.io) {
        val viewModel = mockSuccessfulCase()
       // viewModel.fetchCurrencyInformation()
        viewModel.outputs.fetchUIStateFlow().test {
            assertTrue(awaitItem() is CurrencyListPageState.Loading)
            cancelAndConsumeRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun isListSuccessfulStatePropagated() = runTest(testDispatchers.io) {
        val viewModel = mockSuccessfulCase()

        viewModel.inputs.setCurrencyAmount(defaultCurrencyValue)
       // viewModel.inputs.fetchCurrencyInformation()

        viewModel.outputs.fetchUIStateFlow().test {
            val emittedValue = awaitItem()
            assertTrue(emittedValue is CurrencyListPageState.Success && emittedValue.data == transformedCurrencyValues)
            cancelAndConsumeRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun isListErrorStatePropagated() = runTest(testDispatchers.io) {
        val viewModel = mockErrorCase()

      //  viewModel.fetchCurrencyInformation()

        viewModel.outputs.fetchUIStateFlow().test {
            assertTrue(awaitItem() is CurrencyListPageState.Error)
            cancelAndConsumeRemainingEvents()
        }
    }


    @Test
    fun isFetchLatestCurrencyPricesInvoked() = runBlocking(testDispatchers.io) {
        val viewModel = mockSuccessfulCase()

    //    viewModel.fetchCurrencyInformation()

        viewModel.outputs.fetchUIStateFlow().test {
            verify(repository, times(1)).getCurrencyInformation(
                defaultCurrency,
                supportedCurrencies
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun doesUpdatingSpecifiedCurrencyUpdateCurrencyPrices() = runTest(testDispatchers.io) {
        val viewModel = mockSuccessfulCase()

        viewModel.inputs.setCurrencyAmount(defaultCurrencyValue)
    //    viewModel.inputs.fetchCurrencyInformation()

        viewModel.inputs.setCurrencyAmount(1000)

        viewModel.outputs.fetchUIStateFlow().test {
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
            emit(Result.failure(genericRuntimeException))
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
            emit(expectedResult)
        })
        return viewModel
    }
    //endregion
}