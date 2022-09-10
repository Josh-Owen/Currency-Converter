package com.joshowen.forexexchangerates.repositories.fxexchange

import com.joshowen.forexexchangerates.data.Currency
import com.joshowen.forexexchangerates.data.CurrencyHistory
import com.joshowen.forexexchangerates.data.CurrencyType
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate

interface ForeignExchangeRepository {
    fun getCurrencyInformation(
        baseCurrency: CurrencyType,
        supportedCurrencyCodes: String
    ): Flow<Result<List<Currency>>>

    fun getPriceHistory(
        baseCurrency: CurrencyType,
        selectedCurrencyCodes: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<Result<List<CurrencyHistory>>>
}