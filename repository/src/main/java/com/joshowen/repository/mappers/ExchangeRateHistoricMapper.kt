package com.joshowen.repository.mappers

import com.joshowen.repository.data.Currency
import com.joshowen.repository.data.CurrencyHistory
import com.joshowen.repository.enums.CurrencyType
import com.joshowen.repository.retrofit.responses.current.ExchangeRateItemRawResponse

import javax.inject.Inject

internal class ExchangeRateHistoricMapper @Inject constructor(): Function1<Pair<String, ExchangeRateItemRawResponse>, CurrencyHistory> {
    override fun invoke(rawExchangeRates : Pair<String, ExchangeRateItemRawResponse>): CurrencyHistory {
        return CurrencyHistory(
            rawExchangeRates.first, listOf(Currency(CurrencyType.US_DOLLARS, rawExchangeRates.second.USD),
            Currency(CurrencyType.EUROS, rawExchangeRates.second.EUR),
            Currency(CurrencyType.JAPANESE_YEN, rawExchangeRates.second.JPY),
            Currency(CurrencyType.GREAT_BRITISH_POUNDS, rawExchangeRates.second.GBP),
            Currency(CurrencyType.AUSTRALIAN_DOLLARS, rawExchangeRates.second.AUD),
            Currency(CurrencyType.CANADIAN_DOLLARS, rawExchangeRates.second.CAD),
            Currency(CurrencyType.SWISS_FRANC, rawExchangeRates.second.CHF),
            Currency(CurrencyType.CHINESE_YUAN, rawExchangeRates.second.CNY),
            Currency(CurrencyType.SWEDISH_KRONA, rawExchangeRates.second.SEK),
            Currency(CurrencyType.NEW_ZEALAND_DOLLARS, rawExchangeRates.second.NZD))
        )
    }
}


