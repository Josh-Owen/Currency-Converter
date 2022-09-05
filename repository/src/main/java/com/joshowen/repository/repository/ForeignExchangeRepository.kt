package com.joshowen.repository.repository

import com.joshowen.repository.data.Currency
import com.joshowen.repository.data.CurrencyHistory
import com.joshowen.repository.enums.CurrencyType
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate

interface ForeignExchangeRepository {
    fun getCurrencyInformation(baseCurrency : CurrencyType, supportedCurrencyCodes : String) : Flow<Result<List<Currency>>>
    fun getPriceHistory(baseCurrency : CurrencyType, selectedCurrencyCodes : String, startDate : LocalDate, endDate : LocalDate): Flow<Result<List<CurrencyHistory>>>
}