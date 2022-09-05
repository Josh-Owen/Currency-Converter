package com.joshowen.forexexchangerates.ui.currencylist

import com.joshowen.repository.data.Currency
import com.joshowen.repository.enums.CurrencyType

sealed class CurrencyListPageState {
    data class Success(val data: List<Currency>) : CurrencyListPageState()
    data class Error(val message: String) : CurrencyListPageState()
    object Loading : CurrencyListPageState()
}