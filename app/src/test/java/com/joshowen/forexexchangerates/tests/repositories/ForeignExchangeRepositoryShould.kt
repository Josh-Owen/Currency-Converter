package com.joshowen.forexexchangerates.tests.repositories

import com.joshowen.forexexchangerates.base.BaseUnitTest
import com.joshowen.forexexchangerates.data.CurrencyType
import com.joshowen.forexexchangerates.mappers.ExchangeRateHistoricMapper
import com.joshowen.forexexchangerates.mappers.ExchangeRateMapper
import com.joshowen.forexexchangerates.repositories.fxexchange.ForeignExchangeRepositoryImpl
import com.joshowen.forexexchangerates.retrofit.apis.FX_API_ERROR_CODE_API_LIMIT_EXCEEDED
import com.joshowen.forexexchangerates.retrofit.apis.ForeignExchangeAPI
import com.joshowen.forexexchangerates.retrofit.fxexchange.responses.currentprices.ExchangeRateItemRawResponse
import com.joshowen.forexexchangerates.retrofit.fxexchange.responses.currentprices.ExchangeRateResponse
import com.joshowen.forexexchangerates.retrofit.fxexchange.responses.historicprices.FetchHistoricTimeSeriesResult
import com.joshowen.forexexchangerates.retrofit.wrappers.ApiError
import com.joshowen.forexexchangerates.retrofit.wrappers.ApiException
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.threeten.bp.LocalDate
import retrofit2.Response

class ForeignExchangeRepositoryShould : BaseUnitTest() {

    private val api: ForeignExchangeAPI = mock()

    private val exchangeMapper: ExchangeRateMapper = mock()

    private val historicExchangeMapper: ExchangeRateHistoricMapper = mock()

    private val genericRuntimeException = RuntimeException("Something went wrong.")

    private val startDate = LocalDate.now().minusDays(5)

    private val endDate = LocalDate.now()

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

    private val defaultCurrency = CurrencyType.EUROS.currencyCode

    private val expectedHistoricRawResponse: FetchHistoricTimeSeriesResult = mock()

    private val expectedFetchHistoricTimeSeriesResponse: Response<FetchHistoricTimeSeriesResult> =
        Response.success(expectedHistoricRawResponse)

    private val expectedFetchResultsRawResponse: ExchangeRateItemRawResponse = mock()

    private val expectedCurrentPriceRawResponse: ExchangeRateResponse = mock()

    private val expectedFetchLatestConversionsResponse: Response<ExchangeRateResponse> =
        Response.success(expectedCurrentPriceRawResponse)

    private val specifiedDate = "2022-08-13"

    private val dateAndExchangeRatePair = Pair(specifiedDate, expectedFetchResultsRawResponse)

    private val mappedHistoricItems =
        historicExchangeMapper.invoke(Pair(specifiedDate, expectedFetchResultsRawResponse))

    private val mappedExchangeItems = exchangeMapper.invoke(expectedFetchResultsRawResponse)


    private lateinit var repository: ForeignExchangeRepositoryImpl

    @Before
    fun setup() {
        repository = ForeignExchangeRepositoryImpl(api, exchangeMapper, historicExchangeMapper)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun isFetchLatestPricesFromAPIServiceExecuted() = runTest {
        fetchCurrencyInformationSuccess()
        repository.getCurrencyInformation(CurrencyType.EUROS, supportedCurrencies).first()
        verify(api, times(1)).fetchLatestPricesForSymbols(
            supportedCurrencies,
            CurrencyType.EUROS.currencyCode
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun doesFetchLatestPricesPropagateNetworkErrors() = runTest {
        fetchCurrencyInformationGenericException()

        val response =
            repository.getCurrencyInformation(CurrencyType.EUROS, supportedCurrencies).first()
        assertTrue((response is ApiException) && genericRuntimeException.message == response.e.message)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun doesPriceHistoryPropagateApiLimitExceededError() = runTest {
        fetchCurrencyHistoryApiLimitExceeded()
        val response =
            repository.getPriceHistory(CurrencyType.EUROS, supportedCurrencies, startDate, endDate)
                .first()
        assertTrue(response is ApiError && response.code == FX_API_ERROR_CODE_API_LIMIT_EXCEEDED)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun doesFetchLatestPricesPropagateApiLimitExceededError() = runTest {
        fetchCurrencyPricesApiLimitExceeded()
        val response =
            repository.getCurrencyInformation(CurrencyType.EUROS, supportedCurrencies)
                .first()
        assertTrue(response is ApiError && response.code == FX_API_ERROR_CODE_API_LIMIT_EXCEEDED)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun isFetchPriceHistoryFromAPIServiceExecuted() = runTest {
        fetchCurrencyHistorySuccess()
        repository.getPriceHistory(CurrencyType.EUROS, supportedCurrencies, startDate, endDate)
            .first()
        verify(api, times(1)).fetchHistoricPricesForSymbol(
            startDate,
            endDate,
            supportedCurrencies,
            CurrencyType.EUROS.currencyCode,
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun doesFetchPriceHistoryPropagateNetworkError() = runTest {
        fetchCurrencyHistoryGenericException()
        val response =
            repository.getPriceHistory(CurrencyType.EUROS, supportedCurrencies, startDate, endDate)
                .first()
        assertTrue(response is ApiException && genericRuntimeException.message == response.e.message)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun doesFetchPriceHistoryDelegateMappingToMapper() = runTest {
        fetchCurrencyHistorySuccess()
        repository.getPriceHistory(CurrencyType.EUROS, supportedCurrencies, startDate, endDate)
            .first()
        Mockito.verify(historicExchangeMapper, times(1)).invoke(dateAndExchangeRatePair)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun doesFetchLatestPriceDelegateMappingToMapper() = runTest {
        fetchCurrencyInformationSuccess()
        repository.getCurrencyInformation(CurrencyType.EUROS, supportedCurrencies).first()
        Mockito.verify(exchangeMapper, times(1)).invoke(expectedFetchResultsRawResponse)
    }

    //region Test Cases

    private suspend fun fetchCurrencyInformationGenericException() {
        whenever(api.fetchLatestPricesForSymbols(supportedCurrencies, defaultCurrency)).thenThrow(
            genericRuntimeException
        )
    }

    private suspend fun fetchCurrencyInformationSuccess() {

        whenever(api.fetchLatestPricesForSymbols(supportedCurrencies, defaultCurrency))
            .thenReturn(expectedFetchLatestConversionsResponse)

        whenever(exchangeMapper.invoke(expectedFetchResultsRawResponse)).thenReturn(
            mappedExchangeItems
        )
    }

    private suspend fun fetchCurrencyHistoryApiLimitExceeded() {

        whenever(
            api.fetchHistoricPricesForSymbol(
                startDate,
                endDate,
                supportedCurrencies,
                defaultCurrency,
            )
        ).thenReturn(Response.error(429, "".toResponseBody()))

        whenever(historicExchangeMapper.invoke(dateAndExchangeRatePair)).thenReturn(
            mappedHistoricItems
        )

    }

    private suspend fun fetchCurrencyPricesApiLimitExceeded() {
        whenever(
            api.fetchLatestPricesForSymbols(
                supportedCurrencies, defaultCurrency
            )
        ).thenReturn(Response.error(429, "".toResponseBody()))

        whenever(historicExchangeMapper.invoke(dateAndExchangeRatePair)).thenReturn(
            mappedHistoricItems
        )
    }

    private suspend fun fetchCurrencyHistoryGenericException() {
        whenever(
            api.fetchHistoricPricesForSymbol(
                startDate,
                endDate,
                supportedCurrencies,
                defaultCurrency,
            )
        ).thenThrow(genericRuntimeException)
    }

    private suspend fun fetchCurrencyHistorySuccess() {
        whenever(
            api.fetchHistoricPricesForSymbol(
                startDate,
                endDate,
                supportedCurrencies,
                defaultCurrency,
            )
        ).thenReturn(expectedFetchHistoricTimeSeriesResponse)

        whenever(historicExchangeMapper.invoke(dateAndExchangeRatePair)).thenReturn(
            mappedHistoricItems
        )
    }
    //endregion
}