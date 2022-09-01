package com.joshowen.forexexchangerates.test

import android.app.Application
import app.cash.turbine.test
import com.joshowen.forexexchangerates.TestDispatchers
import com.joshowen.forexexchangerates.base.BaseUnitTest
import com.joshowen.forexexchangerates.ui.currencylist.CurrencyListFragmentVM
import com.joshowen.forexexchangerates.ui.currencylist.CurrencyListPageState
import com.joshowen.repository.data.Currency
import com.joshowen.repository.enums.CurrencyType
import com.joshowen.repository.repository.ForeignExchangeRepositoryImpl
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.take
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

    var expectedResponseFromRepository: List<Currency> = mock()

    var mockedListOfCurrencies: List<Pair<CurrencyType, String>> = mock()

    var expectedResult = Result.success(expectedResponseFromRepository)

    var application : Application = mock()

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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun isFetchLatestCurrencyPricesInvoked() = runTest {
        val viewModel = mockSuccessfulCase()

        viewModel.outputs.fetchUiState()
            .test {
                awaitItem()
                verify(repository, times(1)).getCurrencyInformation(
                    defaultCurrency,
                    supportedCurrencies
                )
                cancelAndConsumeRemainingEvents()
            }

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun doesEmitDefaultApplicationCurrency() = runBlocking(testDispatchers.main) {
        val viewModel = mockSuccessfulCase()
        viewModel.outputs.fetchDefaultApplicationCurrency().testWithScheduler {
            val emission = awaitItem()
            assertEquals(defaultCurrency.currencyCode, emission)
            cancelAndConsumeRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun isLatestPriceErrorPropagatedFromRepository() = runTest {

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun doesUpdatingCurrencyUpdateViewModelState() = runTest {
        val viewModel = mockSuccessfulCase()
        val priorToUpdatingAmount = viewModel.amountToConvert.value
        assertEquals(defaultCurrencyValue, priorToUpdatingAmount)
        viewModel.inputs.setCurrencyAmount(200)
        val updatedAmount = viewModel.amountToConvert.value
        assertEquals(updatedAmount, 200)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun showSpinnerWhilstLoading() = runBlocking {
            val viewModel = mockSuccessfulCase()
            viewModel.inputs.setCurrencyAmount(105)
            viewModel.outputs
                .fetchUiState()
                .testWithScheduler {
                    val emission = awaitItem()
                    assertEquals(true, emission is CurrencyListPageState.Loading)
                    awaitComplete()
                }

    }
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun hidesSpinnerAfterLoading() = runTest {
        val viewModel = mockSuccessfulCase()
        viewModel.inputs.setCurrencyAmount(105)
        viewModel.outputs
            .fetchUiState()
            .testWithScheduler {

                advanceTimeBy(5000)
                val emission = awaitItem()
                assertEquals(true, emission is CurrencyListPageState.Loading)
                awaitComplete()
            }
    }

    //endregion

    // region Test Cases
    private suspend fun mockErrorCase(): CurrencyListFragmentVM {
        val viewModel = CurrencyListFragmentVM(application,testDispatchers, repository)
        whenever(repository.getCurrencyInformation(defaultCurrency, supportedCurrencies)
        ).thenReturn(flow {
            emit(Result.failure(genericRuntimeException))
        })
        return viewModel
    }

    private suspend fun mockSuccessfulCase(): CurrencyListFragmentVM {
        val viewModel = CurrencyListFragmentVM(application, testDispatchers, repository)

        whenever(repository.getCurrencyInformation(defaultCurrency, supportedCurrencies)).thenReturn(flow {
            emit(expectedResult)
        })
        return viewModel
    }

    //endregion
}