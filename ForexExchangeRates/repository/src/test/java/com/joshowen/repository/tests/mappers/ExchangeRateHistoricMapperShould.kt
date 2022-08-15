package com.joshowen.repository.tests.mappers

import com.joshowen.repository.base.BaseUnitTest
import com.joshowen.repository.data.CurrencyHistory
import com.joshowen.repository.data.CurrencyHistoryItem
import com.joshowen.repository.mappers.ExchangeRateHistoricMapper
import com.joshowen.repository.retrofit.result.ExchangeRateItemRawResponse
import junit.framework.TestCase.assertEquals
import org.junit.Test

class ExchangeRateHistoricMapperShould : BaseUnitTest() {

    private val mapper = ExchangeRateHistoricMapper()

    private val expectedHistoricDateString = "2022-08-12"

    private val preProcessedExchangeRates = ExchangeRateItemRawResponse(
        USD = 1.0,
        EUR = 2.0,
        JPY = 3.0,
        GBP = 4.0,
        AUD = 5.0,
        CAD = 6.0,
        CHF = 7.0,
        CNY = 8.0,
        SEK = 9.0,
        NZD = 10.0
    )

    private val expectedCurrencyHistoryItem = CurrencyHistoryItem(
        USD = 1.0,
        EUR = 2.0,
        JPY = 3.0,
        GBP = 4.0,
        AUD = 5.0,
        CAD = 6.0,
        CHF = 7.0,
        CNY = 8.0,
        SEK = 9.0,
        NZD = 10.0
    )

    private val historicDateAndExchangeRatePair = Pair(expectedHistoricDateString, preProcessedExchangeRates)

    private val mappedResponse = mapper.invoke(historicDateAndExchangeRatePair)

    private val expectedResponse = CurrencyHistory(expectedHistoricDateString, expectedCurrencyHistoryItem)

    @Test
    fun hasExpectedDatePreMapping() {
        assertEquals(mappedResponse.date, expectedResponse.date)
    }


    @Test
    fun maintainsExpectedUSDValuePreMapping() {
        assertEquals(expectedResponse.currencyPriceHistory.USD, mappedResponse.currencyPriceHistory.USD)
    }

    @Test
    fun maintainsExpectedEurosValuePreMapping() {
        assertEquals(expectedResponse.currencyPriceHistory.EUR, mappedResponse.currencyPriceHistory.EUR)
    }

    @Test
    fun maintainsExpectedJapaneseYenValuePreMapping() {
        assertEquals(expectedResponse.currencyPriceHistory.JPY, mappedResponse.currencyPriceHistory.JPY)
    }

    @Test
    fun maintainsExpectedGreatBritishPoundsValuePreMapping() {
        assertEquals(expectedResponse.currencyPriceHistory.GBP, mappedResponse.currencyPriceHistory.GBP)
    }

    @Test
    fun maintainsExpectedAustralianDollarsValuePreMapping() {
        assertEquals(expectedResponse.currencyPriceHistory.AUD, mappedResponse.currencyPriceHistory.AUD)
    }

    @Test
    fun maintainsExpectedCanadianDollarsValuePreMapping() {
        assertEquals(expectedResponse.currencyPriceHistory.CAD, mappedResponse.currencyPriceHistory.CAD)
    }

    @Test
    fun maintainsExpectedSwissFrancValuePreMapping() {
        assertEquals(expectedResponse.currencyPriceHistory.CHF, mappedResponse.currencyPriceHistory.CHF)
    }

    @Test
    fun maintainsExpectedChineseYuanValuePreMapping() {
        assertEquals(expectedResponse.currencyPriceHistory.CNY, mappedResponse.currencyPriceHistory.CNY)
    }

    @Test
    fun maintainsExpectedSwedishKronaValuePreMapping() {
        assertEquals(expectedResponse.currencyPriceHistory.SEK, mappedResponse.currencyPriceHistory.SEK)
    }

    @Test
    fun maintainsExpectedNewZealandDollarsValuePreMapping() {
        assertEquals(expectedResponse.currencyPriceHistory.NZD, mappedResponse.currencyPriceHistory.NZD)
    }

}