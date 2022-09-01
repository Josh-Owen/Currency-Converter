package com.joshowen.repository.tests.repositories

import com.joshowen.repository.base.BaseUnitTest
import com.joshowen.repository.enums.CurrencyType
import com.joshowen.repository.mappers.ExchangeRateHistoricMapper
import com.joshowen.repository.mappers.ExchangeRateMapper
import com.joshowen.repository.repository.ForeignExchangeRepositoryImpl
import com.joshowen.repository.retrofit.ForeignExchangeAPI
import com.joshowen.repository.retrofit.responses.current.ExchangeRateItemRawResponse
import com.joshowen.repository.retrofit.responses.current.ExchangeRateResponse
import com.joshowen.repository.retrofit.responses.historic.FetchHistoricTimeSeriesResult
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.Mockito
import java.lang.RuntimeException

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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun isFetchLatestPricesFromAPIServiceExecuted() = runTest {
        val repository = fetchCurrencyInformationSuccess()
        repository.getCurrencyInformation(CurrencyType.EUROS, supportedCurrencies).first()
        verify(api, times(1)).fetchLatestPricesForSymbols(
            supportedCurrencies,
            CurrencyType.EUROS.currencyCode
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun doesFetchLatestPricesPropagateNetworkErrors() = runTest {
        val repository = fetchCurrencyInformationGenericException()
        assertEquals(
            genericRuntimeException.message,
            repository.getCurrencyInformation(CurrencyType.EUROS, supportedCurrencies).first()
                .exceptionOrNull()?.message
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun isFetchPriceHistoryFromAPIServiceExecuted() = runTest {
        val repository = fetchCurrencyHistoryInformationSuccess()
        repository.getPricedHistory(CurrencyType.EUROS, supportedCurrencies, startDate, endDate)
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
        val repository = fetchCurrencyHistoryInformationException()
        assertEquals(
            genericRuntimeException.message,
            repository.getPricedHistory(CurrencyType.EUROS, supportedCurrencies, startDate, endDate)
                .first()
                .exceptionOrNull()?.message
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun doesFetchPriceHistoryDelegateMappingToMapper() = runTest {
        val repository = fetchCurrencyHistoryInformationSuccess()
        repository.getPricedHistory(CurrencyType.EUROS, supportedCurrencies, startDate, endDate)
            .first()
        Mockito.verify(historicExchangeMapper, times(1)).invoke(dateAndExchangeRatePair)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun doesFetchLatestPriceDelegateMappingToMapper() = runTest {
        val repository = fetchCurrencyInformationSuccess()
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

    private suspend fun fetchCurrencyInformationSuccess(): ForeignExchangeRepositoryImpl {
        val repository = ForeignExchangeRepositoryImpl(api, exchangeMapper, historicExchangeMapper)

        whenever(api.fetchLatestPricesForSymbols(supportedCurrencies, defaultCurrency))
            .thenReturn(expectedFetchLatestConversionsResponse)

        whenever(exchangeMapper.invoke(expectedFetchResultsRawResponse)).thenReturn(
            mappedExchangeItems
        )
        return repository
    }

    private suspend fun fetchCurrencyHistoryInformationException(): ForeignExchangeRepositoryImpl {
        val repository = ForeignExchangeRepositoryImpl(api, exchangeMapper, historicExchangeMapper)
        whenever(
            api.fetchHistoricPricesForSymbol(
                startDate,
                endDate,
                supportedCurrencies,
                defaultCurrency,
            )
        ).thenThrow(genericRuntimeException)
        return repository
    }

    private suspend fun fetchCurrencyHistoryInformationSuccess(): ForeignExchangeRepositoryImpl {
        val repository = ForeignExchangeRepositoryImpl(api, exchangeMapper, historicExchangeMapper)
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

        return repository
    }
    //endregion
}