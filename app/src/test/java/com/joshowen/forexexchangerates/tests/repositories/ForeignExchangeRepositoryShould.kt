package com.joshowen.forexexchangerates.tests.repositories

import com.joshowen.forexexchangerates.base.BaseUnitTest
import com.joshowen.forexexchangerates.data.CurrencyType
import com.joshowen.forexexchangerates.mappers.ExchangeRateHistoricMapper
import com.joshowen.forexexchangerates.mappers.ExchangeRateMapper
import com.joshowen.forexexchangerates.repositories.fxexchange.ForeignExchangeRepositoryImpl
import com.joshowen.forexexchangerates.retrofit.apis.ForeignExchangeAPI
import com.joshowen.forexexchangerates.retrofit.fxexchange.responses.currentprices.ExchangeRateItemRawResponse
import com.joshowen.forexexchangerates.retrofit.fxexchange.responses.currentprices.ExchangeRateResponse
import com.joshowen.forexexchangerates.retrofit.fxexchange.responses.historicprices.FetchHistoricTimeSeriesResult
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ForeignExchangeRepositoryShould : BaseUnitTest() {

    private val api: ForeignExchangeAPI = mock()

    private val exchangeMapper: ExchangeRateMapper = mock()

    private val historicExchangeMapper: ExchangeRateHistoricMapper = mock()

    private val genericRuntimeException = RuntimeException("Something went wrong.")

    private val startDate = org.threeten.bp.LocalDate.now().minusDays(5)

    private val endDate = org.threeten.bp.LocalDate.now().minusDays(5)

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

    private val expectedFetchHistoricTimeSeriesResponse: FetchHistoricTimeSeriesResult = mock()

    private val expectedFetchResultsRawResponse: ExchangeRateItemRawResponse = mock()

    private val expectedFetchLatestConversionsResponse: ExchangeRateResponse = mock()

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
        assertEquals(
            genericRuntimeException.message,
            repository.getCurrencyInformation(CurrencyType.EUROS, supportedCurrencies).first()
                .exceptionOrNull()?.message
        )
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
    fun doesFetchPriceHistoryPropagateNetworkErrors() = runTest {
        fetchCurrencyHistoryGenericException()
        assertEquals(
            genericRuntimeException.message,
            repository.getPriceHistory(CurrencyType.EUROS, supportedCurrencies, startDate, endDate)
                .first()
                .exceptionOrNull()?.message
        )
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

    private suspend fun fetchCurrencyInformationGenericException(): ForeignExchangeRepositoryImpl {
        val repository = ForeignExchangeRepositoryImpl(api, exchangeMapper, historicExchangeMapper)
        whenever(api.fetchLatestPricesForSymbols(supportedCurrencies, defaultCurrency)).thenThrow(
            genericRuntimeException
        )
        return repository
    }

    private suspend fun fetchCurrencyInformationSuccess() {

        whenever(api.fetchLatestPricesForSymbols(supportedCurrencies, defaultCurrency))
            .thenReturn(expectedFetchLatestConversionsResponse)

        whenever(exchangeMapper.invoke(expectedFetchResultsRawResponse)).thenReturn(
            mappedExchangeItems
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