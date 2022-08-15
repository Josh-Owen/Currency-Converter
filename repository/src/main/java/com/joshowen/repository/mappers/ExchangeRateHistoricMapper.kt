package com.joshowen.repository.mappers

import com.joshowen.repository.data.CurrencyHistory
import com.joshowen.repository.data.CurrencyHistoryItem
import com.joshowen.repository.retrofit.result.ExchangeRateItemRawResponse

import javax.inject.Inject

class ExchangeRateHistoricMapper @Inject constructor(): Function1<Pair<String, ExchangeRateItemRawResponse>, CurrencyHistory> {
    override fun invoke(rawExchangeRates: Pair<String, ExchangeRateItemRawResponse>): CurrencyHistory {
        return CurrencyHistory(
            rawExchangeRates.first, CurrencyHistoryItem(
                USD = rawExchangeRates.second.USD,
                EUR = rawExchangeRates.second.EUR,
                JPY = rawExchangeRates.second.JPY,
                GBP = rawExchangeRates.second.GBP,
                AUD = rawExchangeRates.second.AUD,
                CAD = rawExchangeRates.second.CAD,
                CHF = rawExchangeRates.second.CHF,
                CNY = rawExchangeRates.second.CNY,
                SEK = rawExchangeRates.second.SEK,
                NZD = rawExchangeRates.second.NZD
            )
        )
    }
}
