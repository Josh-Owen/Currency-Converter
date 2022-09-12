package com.joshowen.forexexchangerates.tests.viewmodels

import android.app.Application
import app.cash.turbine.test
import com.joshowen.forexexchangerates.R
import com.joshowen.forexexchangerates.base.BaseUnitTest
import com.joshowen.forexexchangerates.base.DEFAULT_APP_CURRENCY
import com.joshowen.forexexchangerates.base.SUPPORTED_CURRENCIES
import com.joshowen.forexexchangerates.data.Currency
import com.joshowen.forexexchangerates.data.CurrencyType
import com.joshowen.forexexchangerates.repositories.fxexchange.ForeignExchangeRepositoryImpl
import com.joshowen.forexexchangerates.retrofit.apis.FX_API_ERROR_CODE_API_LIMIT_EXCEEDED
import com.joshowen.forexexchangerates.retrofit.wrappers.ApiError
import com.joshowen.forexexchangerates.retrofit.wrappers.ApiException
import com.joshowen.forexexchangerates.retrofit.wrappers.ApiSuccess
import com.joshowen.forexexchangerates.ui.currencylist.CurrencyListFragmentVM
import com.joshowen.forexexchangerates.ui.currencylist.CurrencyListPageState
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

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

    private val genericNetworkMessage =
        "Oops! Something went wrong, do you have an active network connection?"

    private val genericRuntimeException = RuntimeException(genericNetworkMessage)

    private val defaultCurrency = CurrencyType.EUROS

    private val defaultCurrencyValue = 100
    private val updatedCurrencyValue = 1000

    private val apiLimitExceeded = "Monthly API call limit exceeded."

    private lateinit var viewModel: CurrencyListFragmentVM

    //endregion


    @Before
    fun setup() {
        viewModel = CurrencyListFragmentVM(application, testDispatchers, repository)
    }


    //region Tests

    @Test
    fun doesEmitDefaultApplicationCurrency() = runBlocking(testDispatchers.main) {
        mockSuccessfulCase()

        viewModel.outputs.fetchDefaultApplicationCurrencyFlow().test {
            val emission = awaitItem()
            assertEquals(defaultCurrency.currencyCode, emission)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun doesUpdatingCurrencyFieldUpdateViewModelState() = runBlocking(testDispatchers.io) {
        mockSuccessfulCase()

        val priorToUpdatingAmount = viewModel.outputs.fetchSpecifiedAmountOfCurrency()
        assertEquals(defaultCurrencyValue, priorToUpdatingAmount)
        viewModel.inputs.setCurrencyAmount(200)
        val updatedAmount = viewModel.fetchSpecifiedAmountOfCurrency()
        assertEquals(updatedAmount, 200)
    }

    @Test
    fun isListLoadingStatePropagated() = runBlocking(testDispatchers.io) {
        mockSuccessfulCase()

        viewModel.inputs.fetchCurrencyInformation()
        viewModel.outputs.fetchUIStateFlow().test {
            val emittedValue = awaitItem()
            assertTrue(emittedValue is CurrencyListPageState.Loading)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun isListSuccessfulStatePropagated() = runBlocking(testDispatchers.io) {
        mockSuccessfulCase()

        viewModel.inputs.setCurrencyAmount(defaultCurrencyValue)
        viewModel.inputs.fetchCurrencyInformation()
        viewModel.outputs.fetchUIStateFlow().test {
            awaitItem()
            val emittedValue = awaitItem()
            assertTrue(emittedValue is CurrencyListPageState.Success && emittedValue.data == transformedCurrencyValues)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun isApiLimitExceededErrorPropagated() = runBlocking(testDispatchers.io) {
        mockApiLimitExceededCase()

        viewModel.inputs.fetchCurrencyInformation()
        viewModel.outputs.fetchUIStateFlow().test {
            awaitItem()
            val emittedValue = awaitItem()
            assertTrue(emittedValue is CurrencyListPageState.Error && emittedValue.message == apiLimitExceeded)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun isListErrorStatePropagated() = runBlocking(testDispatchers.io) {
        mockErrorCase()
        viewModel.outputs.fetchUIStateFlow().test {
            val emittedValue = awaitItem()
            assertTrue(emittedValue is CurrencyListPageState.Error)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun doesUpdatingSpecifiedCurrencyUpdateCurrencyPrices() = runBlocking(testDispatchers.io) {
        mockSuccessfulCase()

        viewModel.inputs.fetchCurrencyInformation()
        viewModel.inputs.setCurrencyAmount(updatedCurrencyValue)
        viewModel.outputs.fetchUIStateFlow().test {
            awaitItem()
            val emittedValue = awaitItem()
            assertTrue(emittedValue is CurrencyListPageState.Success && emittedValue.data == updatedCurrencyValues)
            cancelAndConsumeRemainingEvents()
        }
    }

    //endregion

    // region Test Cases
    private suspend fun mockErrorCase() {
        whenever(
            repository.getCurrencyInformation(
                DEFAULT_APP_CURRENCY,
                SUPPORTED_CURRENCIES
            )
        ).thenReturn(flow {
            emit(ApiException(genericRuntimeException))
        })
    }

    private suspend fun mockApiLimitExceededCase() {
        whenever(
            repository.getCurrencyInformation(
                DEFAULT_APP_CURRENCY,
                SUPPORTED_CURRENCIES
            )
        ).thenReturn(flow {
            emit(ApiError(FX_API_ERROR_CODE_API_LIMIT_EXCEEDED, apiLimitExceeded))
        })

        whenever(
            application.getString(
                R.string.network_error_api_call_limit
            )
        ).thenReturn(apiLimitExceeded)
    }


    private suspend fun mockSuccessfulCase() {
        whenever(
            repository.getCurrencyInformation(
                DEFAULT_APP_CURRENCY,
                SUPPORTED_CURRENCIES
            )
        ).thenReturn(
            flow {
                emit(ApiSuccess(originalCurrencyValues))
            })
    }
    //endregion
}
