package com.joshowen.forexexchangerates.tests.viewmodels

import android.app.Application
import app.cash.turbine.test
import com.joshowen.forexexchangerates.base.BaseUnitTest
import com.joshowen.forexexchangerates.data.Currency
import com.joshowen.forexexchangerates.data.CurrencyHistory
import com.joshowen.forexexchangerates.data.CurrencyType
import com.joshowen.forexexchangerates.repositories.fxexchange.ForeignExchangeRepositoryImpl
import com.joshowen.forexexchangerates.ui.currencyhistory.CurrencyHistoryFragmentVM
import com.joshowen.forexexchangerates.ui.currencyhistory.CurrencyHistoryPageState
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.threeten.bp.LocalDate

class CurrencyHistoryViewModelShould : BaseUnitTest() {

    private val repository: ForeignExchangeRepositoryImpl = mock()

    private val defaultCurrency = CurrencyType.EUROS

    private val selectedCurrencies = listOf(
        CurrencyType.US_DOLLARS,
        CurrencyType.JAPANESE_YEN,
        CurrencyType.GREAT_BRITISH_POUNDS,
    )

    private val selectedCurrenciesString = listOf(
        CurrencyType.US_DOLLARS.currencyCode,
        CurrencyType.JAPANESE_YEN.currencyCode,
        CurrencyType.GREAT_BRITISH_POUNDS.currencyCode,
    ).joinToString()

    private val startDate = LocalDate.now().minusDays(5)
    private val endDate = LocalDate.now()

    private val genericRuntimeException = RuntimeException("Something went wrong.")

    private var mockedListOfCurrencyHistory: List<CurrencyHistory> = listOf(
        CurrencyHistory(
            "2022-09-03", listOf(
                Currency(CurrencyType.GREAT_BRITISH_POUNDS, 0.0),
                Currency(CurrencyType.US_DOLLARS, 1000.0),
                Currency(CurrencyType.JAPANESE_YEN, 2000.0)
            )
        )
    )

    var application: Application = mock()

    private var expectedResponse = Result.success(mockedListOfCurrencyHistory)

    private val userSpecifiedAmountOfCurrency = "100"

    private val expectedSpecifiedCurrencyOutput =
        "${defaultCurrency.currencyCode} $userSpecifiedAmountOfCurrency"


    private lateinit var viewModel : CurrencyHistoryFragmentVM

    @Before
    fun setup() {
        viewModel = CurrencyHistoryFragmentVM(application, testDispatchers, repository)
    }

    @Test
    fun doesDisplayDefaultCurrencyAndUserSpecifiedAmount() = runBlocking(testDispatchers.io) {
        mockSuccessfulCase()
        viewModel.inputs.setSpecifiedCurrencyAmount(userSpecifiedAmountOfCurrency)
        val output = viewModel.outputs.fetchSpecifiedCurrencyAmountFlow().last()
        assertEquals(expectedSpecifiedCurrencyOutput, output)
    }

    @Test
    fun isListLoadingStatePropagated() = runBlocking(testDispatchers.io) {
        mockSuccessfulCase()

        viewModel.inputs.setStartDate(startDate)
        viewModel.inputs.setEndDateRange(endDate)

        viewModel.inputs.setSupportedCurrencies(selectedCurrencies)
        viewModel.inputs.setSpecifiedCurrencyAmount(userSpecifiedAmountOfCurrency)
        viewModel.inputs.fetchPriceHistory()

        viewModel.outputs.fetchUiStateFlow().test {
            assertTrue(awaitItem() is CurrencyHistoryPageState.Loading)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun isListSuccessfulStatePropagated() = runBlocking(testDispatchers.io) {
        mockSuccessfulCase()

        viewModel.inputs.setSupportedCurrencies(selectedCurrencies)
        viewModel.inputs.setSpecifiedCurrencyAmount(defaultCurrency.toString())
        viewModel.inputs.setStartDate(startDate)
        viewModel.inputs.setEndDateRange(endDate)
        viewModel.inputs.fetchPriceHistory()

        viewModel.outputs.fetchUiStateFlow().test {
            assertTrue(awaitItem() is CurrencyHistoryPageState.Loading)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun isListErrorStatePropagated() = runBlocking(testDispatchers.io) {
        mockErrorCase()

        viewModel.inputs.setSupportedCurrencies(selectedCurrencies)
        viewModel.inputs.setSpecifiedCurrencyAmount(defaultCurrency.toString())
        viewModel.inputs.setStartDate(startDate)
        viewModel.inputs.setEndDateRange(endDate)
        viewModel.inputs.fetchPriceHistory()

        viewModel.outputs.fetchUiStateFlow().test {
            assertTrue(awaitItem() is CurrencyHistoryPageState.Loading)
            cancelAndConsumeRemainingEvents()
        }
    }

    // region Test Cases
    private suspend fun mockErrorCase() {
        whenever(
            repository.getPriceHistory(
                defaultCurrency,
                selectedCurrenciesString,
                startDate,
                endDate
            )
        ).thenReturn(
            flow {
                emit(Result.failure(genericRuntimeException))
            })
    }

    private suspend fun mockSuccessfulCase() {
        whenever(
            repository.getPriceHistory(
                defaultCurrency,
                selectedCurrenciesString,
                startDate,
                endDate
            )
        ).thenReturn(
            flow {
                emit(expectedResponse)
            })
    }
    //endregion
}