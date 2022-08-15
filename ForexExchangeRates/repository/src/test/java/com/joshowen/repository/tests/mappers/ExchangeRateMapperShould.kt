package com.joshowen.repository.tests.mappers

import com.joshowen.repository.base.BaseUnitTest
import com.joshowen.repository.data.Currency
import com.joshowen.repository.enums.CurrencyType
import com.joshowen.repository.mappers.ExchangeRateMapper
import com.joshowen.repository.retrofit.result.ExchangeRateItemRawResponse
import junit.framework.TestCase.assertEquals
import org.junit.Test

class ExchangeRateMapperShould : BaseUnitTest() {

    private val mapper = ExchangeRateMapper()

    private val preProcessedExchangeRates : ExchangeRateItemRawResponse = ExchangeRateItemRawResponse(
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

    private val mappedResponse = mapper.invoke(preProcessedExchangeRates)

    private val expectedResponse : List<Currency> = listOf(
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

    @Test
    fun maintainsExpectedUSDValuePreMapping() {
        assertEquals(expectedResponse.first { it.currency == CurrencyType.US_DOLLARS  }.price, mappedResponse.first { it.currency == CurrencyType.US_DOLLARS }.price)
    }

    @Test
    fun maintainsExpectedEurosValuePreMapping() {
        assertEquals(expectedResponse.first { it.currency == CurrencyType.EUROS  }.price, mappedResponse.first { it.currency == CurrencyType.EUROS }.price)
    }

    @Test
    fun maintainsExpectedJapaneseYenValuePreMapping() {
        assertEquals(expectedResponse.first { it.currency == CurrencyType.JAPANESE_YEN  }.price, mappedResponse.first { it.currency == CurrencyType.JAPANESE_YEN }.price)
    }

    @Test
    fun maintainsExpectedGreatBritishPoundsValuePreMapping() {
        assertEquals(expectedResponse.first { it.currency == CurrencyType.GREAT_BRITISH_POUNDS  }.price, mappedResponse.first { it.currency == CurrencyType.GREAT_BRITISH_POUNDS }.price)
    }

    @Test
    fun maintainsExpectedAustralianDollarsValuePreMapping() {
        assertEquals(expectedResponse.first { it.currency == CurrencyType.AUSTRALIAN_DOLLARS  }.price, expectedResponse.first { it.currency == CurrencyType.AUSTRALIAN_DOLLARS }.price)
    }

    @Test
    fun maintainsExpectedCanadianDollarsValuePreMapping() {
        assertEquals(expectedResponse.first { it.currency == CurrencyType.CANADIAN_DOLLARS  }.price, expectedResponse.first { it.currency == CurrencyType.CANADIAN_DOLLARS }.price)
    }

    @Test
    fun maintainsExpectedSwissFrancValuePreMapping() {
        assertEquals(expectedResponse.first { it.currency == CurrencyType.SWISS_FRANC  }.price, expectedResponse.first { it.currency == CurrencyType.SWISS_FRANC }.price)
    }

    @Test
    fun maintainsExpectedChineseYuanValuePreMapping() {
        assertEquals(expectedResponse.first { it.currency == CurrencyType.CHINESE_YUAN  }.price, expectedResponse.first { it.currency == CurrencyType.CHINESE_YUAN }.price)
    }

    @Test
    fun maintainsExpectedSwedishKronaValuePreMapping() {
        assertEquals(expectedResponse.first { it.currency == CurrencyType.SWEDISH_KRONA  }.price, expectedResponse.first { it.currency == CurrencyType.SWEDISH_KRONA }.price)
    }

    @Test
    fun maintainsExpectedNewZealandDollarsValuePreMapping() {
        assertEquals(expectedResponse.first { it.currency == CurrencyType.NEW_ZEALAND_DOLLARS  }.price, expectedResponse.first { it.currency == CurrencyType.NEW_ZEALAND_DOLLARS }.price)
    }



}