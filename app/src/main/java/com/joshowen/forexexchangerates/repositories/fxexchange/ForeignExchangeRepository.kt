package com.joshowen.forexexchangerates.repositories.fxexchange

import com.joshowen.forexexchangerates.data.Currency
import com.joshowen.forexexchangerates.data.CurrencyHistory
import com.joshowen.forexexchangerates.data.CurrencyType
import com.joshowen.forexexchangerates.retrofit.wrappers.ApiResult
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate

interface ForeignExchangeRepository {
    fun getCurrencyInformation(
        baseCurrency: CurrencyType,
        supportedCurrencyCodes: String
    ): Flow<ApiResult<List<Currency>>>

    fun getPriceHistory(
        baseCurrency: CurrencyType,
        selectedCurrencyCodes: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<ApiResult<List<CurrencyHistory>>>
}