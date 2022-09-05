package com.joshowen.forexexchangerates.tests.mappers

import com.joshowen.forexexchangerates.base.BaseUnitTest
import com.joshowen.forexexchangerates.data.Currency
import com.joshowen.forexexchangerates.data.CurrencyHistory
import com.joshowen.forexexchangerates.data.CurrencyType
import com.joshowen.forexexchangerates.mappers.ExchangeRateHistoricMapper
import com.joshowen.forexexchangerates.retrofit.fxexchange.responses.currentprices.ExchangeRateItemRawResponse
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

    private val expectedCurrencyHistory : List<Currency> = listOf(
        Currency(CurrencyType.US_DOLLARS, 1.0),
        Currency(CurrencyType.EUROS, 2.0),
        Currency(CurrencyType.JAPANESE_YEN, 3.0),
        Currency(CurrencyType.GREAT_BRITISH_POUNDS, 4.0),
        Currency(CurrencyType.AUSTRALIAN_DOLLARS, 5.0),
        Currency(CurrencyType.CANADIAN_DOLLARS, 6.0),
        Currency(CurrencyType.SWISS_FRANC, 7.0),
        Currency(CurrencyType.CHINESE_YUAN, 8.0),
        Currency(CurrencyType.SWEDISH_KRONA, 9.0),
        Currency(CurrencyType.NEW_ZEALAND_DOLLARS, 10.0),
    )

    private val historicDateAndExchangeRatePair = Pair(expectedHistoricDateString, preProcessedExchangeRates)

    private val mappedResponse = mapper.invoke(historicDateAndExchangeRatePair)

    private val expectedResponse = CurrencyHistory(expectedHistoricDateString, expectedCurrencyHistory)

    @Test
    fun hasExpectedDatePreMapping() {
        assertEquals(mappedResponse.date, expectedResponse.date)
    }


    @Test
    fun maintainsExpectedUSDValuePreMapping() {
        assertEquals(expectedResponse.currencyPriceHistory.find { it.currency == CurrencyType.US_DOLLARS }, mappedResponse.currencyPriceHistory.find { it.currency == CurrencyType.US_DOLLARS })
    }

    @Test
    fun maintainsExpectedEurosValuePreMapping() {
        assertEquals(expectedResponse.currencyPriceHistory.find { it.currency == CurrencyType.EUROS }, mappedResponse.currencyPriceHistory.find { it.currency == CurrencyType.EUROS })
    }

    @Test
    fun maintainsExpectedJapaneseYenValuePreMapping() {
        assertEquals(expectedResponse.currencyPriceHistory.find { it.currency == CurrencyType.JAPANESE_YEN }, mappedResponse.currencyPriceHistory.find { it.currency == CurrencyType.JAPANESE_YEN })
    }

    @Test
    fun maintainsExpectedGreatBritishPoundsValuePreMapping() {
        assertEquals(expectedResponse.currencyPriceHistory.find { it.currency == CurrencyType.GREAT_BRITISH_POUNDS }, mappedResponse.currencyPriceHistory.find { it.currency == CurrencyType.GREAT_BRITISH_POUNDS })
    }

    @Test
    fun maintainsExpectedAustralianDollarsValuePreMapping() {
        assertEquals(expectedResponse.currencyPriceHistory.find { it.currency == CurrencyType.AUSTRALIAN_DOLLARS }, mappedResponse.currencyPriceHistory.find { it.currency == CurrencyType.AUSTRALIAN_DOLLARS })
    }

    @Test
    fun maintainsExpectedCanadianDollarsValuePreMapping() {
        assertEquals(expectedResponse.currencyPriceHistory.find { it.currency == CurrencyType.CANADIAN_DOLLARS }, mappedResponse.currencyPriceHistory.find { it.currency == CurrencyType.CANADIAN_DOLLARS })
    }

    @Test
    fun maintainsExpectedSwissFrancValuePreMapping() {
        assertEquals(expectedResponse.currencyPriceHistory.find { it.currency == CurrencyType.SWISS_FRANC }, mappedResponse.currencyPriceHistory.find { it.currency == CurrencyType.SWISS_FRANC })
    }

    @Test
    fun maintainsExpectedChineseYuanValuePreMapping() {
        assertEquals(expectedResponse.currencyPriceHistory.find { it.currency == CurrencyType.CHINESE_YUAN }, mappedResponse.currencyPriceHistory.find { it.currency == CurrencyType.CHINESE_YUAN })
    }

    @Test
    fun maintainsExpectedSwedishKronaValuePreMapping() {
        assertEquals(expectedResponse.currencyPriceHistory.find { it.currency == CurrencyType.SWEDISH_KRONA }, mappedResponse.currencyPriceHistory.find { it.currency == CurrencyType.SWEDISH_KRONA })
    }

    @Test
    fun maintainsExpectedNewZealandDollarsValuePreMapping() {
        assertEquals(expectedResponse.currencyPriceHistory.find { it.currency == CurrencyType.NEW_ZEALAND_DOLLARS }, mappedResponse.currencyPriceHistory.find { it.currency == CurrencyType.NEW_ZEALAND_DOLLARS })
    }

}