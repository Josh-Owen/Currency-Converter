package com.joshowen.forexexchangerates.test

import com.joshowen.forexexchangerates.base.BaseUnitTest
import com.joshowen.forexexchangerates.ext.captureValues
import com.joshowen.forexexchangerates.ext.getValueForTest
import com.joshowen.forexexchangerates.ui.currencylist.CurrencyListFragmentVM
import com.joshowen.forexexchangerates.ui.currencylist.CurrencyListPageState
import com.joshowen.repository.data.Currency
import com.joshowen.repository.enums.CurrencyType
import com.joshowen.repository.repository.ForeignExchangeRepositoryImpl
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.lang.RuntimeException
import java.lang.Thread.sleep

class CurrencyListViewModelShould : BaseUnitTest() {

    private val repository: ForeignExchangeRepositoryImpl = mock()

    var expectedResponseFromRepository: List<Currency> = mock()

    var mockedListOfCurrencies: List<Pair<CurrencyType, String>> = mock()

    var expectedResult = Result.success(expectedResponseFromRepository)

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


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun isFetchLatestCurrencyPricesInvoked() = runTest {
        val viewModel = mockSuccessfulCase()
        viewModel.outputs.fetchUiState().captureValues {
            verify(repository, times(1)).getCurrencyInformation(defaultCurrency, supportedCurrencies)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun emitsDefaultApplicationCurrency() = runTest {
        val viewModel = mockSuccessfulCase()
        viewModel.outputs.fetchDefaultApplicationCurrency().captureValues {
            assertEquals(defaultCurrency.currencyCode, values[0])
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun isLatestCurrencyPriceErrorPropagatedFromRepository() = runBlocking {
        val viewModel = mockErrorCase()

            assertEquals(true,  viewModel.outputs.fetchUiState().getValueForTest()  is CurrencyListPageState.Error)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun doesUpdatingCurrencyToConvertUpdateInsideViewModel() = runTest {
        val viewModel = mockSuccessfulCase()
        val priorToUpdatingAmount = viewModel.amountToConvert.value
        assertEquals(defaultCurrencyValue, priorToUpdatingAmount)
        viewModel.inputs.setCurrencyAmount(200)
        val updatedAmount = viewModel.amountToConvert.value
        assertEquals(updatedAmount, 200)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun showSpinnerWhilstLoading() = runTest {
        val viewModel = mockSuccessfulCase()
        viewModel.inputs.setCurrencyAmount(100)
        viewModel.outputs.fetchUiState().captureValues {
            assertEquals(true, values.first() is CurrencyListPageState.Loading)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun hideSpinnerAfterLoadingResults() = runTest(UnconfinedTestDispatcher()) {
        val viewModel = mockSuccessfulCase()
        viewModel.inputs.setCurrencyAmount(100)
        advanceUntilIdle()
        delay(2000)
        assertEquals(true,  viewModel.outputs.fetchUiState().getValueForTest() is CurrencyListPageState.Success)
    }

    // region Test Cases
    private suspend fun mockErrorCase(): CurrencyListFragmentVM {
        val viewModel = CurrencyListFragmentVM(repository)
        whenever(repository.getCurrencyInformation(defaultCurrency, supportedCurrencies)
        ).thenReturn(flow {
            withContext(Dispatchers.IO) { delay(2000) }
            emit(Result.failure(genericRuntimeException))
        })
        return viewModel
    }

    private suspend fun mockSuccessfulCase(): CurrencyListFragmentVM {
        val viewModel = CurrencyListFragmentVM(repository)

        whenever(repository.getCurrencyInformation(defaultCurrency, supportedCurrencies)).thenReturn(flow {
            withContext(Dispatchers.IO) { delay(2000) }
            emit(expectedResult)
        })
        return viewModel
    }

    //endregion
}