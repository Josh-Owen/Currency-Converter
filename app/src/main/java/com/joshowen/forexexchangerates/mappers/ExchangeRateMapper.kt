package com.joshowen.forexexchangerates.mappers

import com.joshowen.forexexchangerates.data.Currency
import com.joshowen.forexexchangerates.data.CurrencyType
import com.joshowen.forexexchangerates.retrofit.fxexchange.responses.currentprices.ExchangeRateItemRawResponse
import javax.inject.Inject

internal class ExchangeRateMapper @Inject constructor(): Function1<ExchangeRateItemRawResponse, List<Currency>> {
    override fun invoke(rawExchangeRates: ExchangeRateItemRawResponse): List<Currency> {
        return listOf(
            Currency(CurrencyType.US_DOLLARS, rawExchangeRates.USD),
            Currency(CurrencyType.EUROS, rawExchangeRates.EUR),
            Currency(CurrencyType.JAPANESE_YEN, rawExchangeRates.JPY),
            Currency(CurrencyType.GREAT_BRITISH_POUNDS, rawExchangeRates.GBP),
            Currency(CurrencyType.AUSTRALIAN_DOLLARS, rawExchangeRates.AUD),
            Currency(CurrencyType.CANADIAN_DOLLARS, rawExchangeRates.CAD),
            Currency(CurrencyType.SWISS_FRANC, rawExchangeRates.CHF),
            Currency(CurrencyType.CHINESE_YUAN, rawExchangeRates.CNY),
            Currency(CurrencyType.SWEDISH_KRONA, rawExchangeRates.SEK),
            Currency(CurrencyType.NEW_ZEALAND_DOLLARS, rawExchangeRates.NZD)
        )
    }
}

