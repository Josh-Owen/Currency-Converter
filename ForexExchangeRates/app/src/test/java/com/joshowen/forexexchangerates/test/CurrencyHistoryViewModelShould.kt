package com.joshowen.forexexchangerates.test

import androidx.lifecycle.asFlow
import com.joshowen.forexexchangerates.base.BaseUnitTest
import com.joshowen.forexexchangerates.ext.captureValues
import com.joshowen.forexexchangerates.ext.getValueForTest
import com.joshowen.forexexchangerates.ui.currencyhistory.CurrencyHistoryFragmentVM
import com.joshowen.forexexchangerates.ui.currencyhistory.CurrencyHistoryPageState
import com.joshowen.repository.data.CurrencyHistory
import com.joshowen.repository.enums.CurrencyType
import com.joshowen.repository.repository.ForeignExchangeRepositoryImpl
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.threeten.bp.LocalDate
import java.lang.RuntimeException

class CurrencyHistoryViewModelShould : BaseUnitTest() {

    private val repository: ForeignExchangeRepositoryImpl = mock()

    private val defaultCurrency = CurrencyType.EUROS

    private val selectedCurrencies = listOf(
        CurrencyType.US_DOLLARS.currencyCode,
        CurrencyType.JAPANESE_YEN.currencyCode,
        CurrencyType.GREAT_BRITISH_POUNDS.currencyCode,
    ).joinToString()

    private val startDate = LocalDate.now().minusDays(5)
    private val endDate = LocalDate.now()

    private val genericRuntimeException = RuntimeException("Something went wrong.")

    var mockedListOfCurrencyHistory: List<CurrencyHistory> = mock()

    private var expectedResponse = Result.success(mockedListOfCurrencyHistory)

    private val userSpecifiedAmountOfCurrency = "100"

    private val expectedSpecifiedCurrencyOutput = "${defaultCurrency.currencyCode} $userSpecifiedAmountOfCurrency"


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun showSpinnerWhilstLoading() = runTest {
        val viewModel = mockSuccessfulCase()
        viewModel.inputs.setSpecifiedCurrencyAmount(userSpecifiedAmountOfCurrency)
        viewModel.inputs.setSupportedCurrencies(listOf(CurrencyType.JAPANESE_YEN))
        viewModel.fetchPriceHistory()
        val state = viewModel.outputs.fetchUiState().getValueForTest() is CurrencyHistoryPageState.Loading
        assertEquals(true, state)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun showsCorrectUserInputCurrencyFormat() = runTest {
        val viewModel = mockSuccessfulCase()
        viewModel.inputs.setSpecifiedCurrencyAmount(userSpecifiedAmountOfCurrency)
        val output = viewModel.outputs.fetchSpecifiedCurrencyAmount().getValueForTest()
        assertEquals(expectedSpecifiedCurrencyOutput, output)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun hidesSpinnerAfterCurrencyHistoryHasBeenProcessed() = runTest {
        val viewModel = mockSuccessfulCase()
        viewModel.inputs.setSpecifiedCurrencyAmount("100")
        viewModel.inputs.setSupportedCurrencies(listOf(CurrencyType.JAPANESE_YEN))
        viewModel.fetchPriceHistory()

        viewModel.outputs.fetchUiState().asFlow().collectLatest {
            assertEquals(true, it)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun isGenericNetworkErrorPropagatedFromRepository() = runTest {
        val viewModel = mockErrorCase()
        viewModel.inputs.setSpecifiedCurrencyAmount("100")
        viewModel.inputs.setSupportedCurrencies(listOf(CurrencyType.JAPANESE_YEN))
        viewModel.fetchPriceHistory()

        viewModel.outputs.fetchUiState().captureValues {
            assertEquals(true, values.last())
        }

    }

    // region Test Cases
    private suspend fun mockErrorCase(): CurrencyHistoryFragmentVM {
        whenever(repository.getPricedHistory(defaultCurrency, selectedCurrencies, startDate, endDate)).thenReturn(
            flow {
                emit(Result.failure(genericRuntimeException))
            })
        return CurrencyHistoryFragmentVM(repository)
    }

    private suspend fun mockSuccessfulCase(): CurrencyHistoryFragmentVM {
        whenever(repository.getPricedHistory(defaultCurrency, selectedCurrencies, startDate, endDate)).thenReturn(
            flow {
            emit(expectedResponse)
        })
        return CurrencyHistoryFragmentVM(repository)
    }

    //endregion

}