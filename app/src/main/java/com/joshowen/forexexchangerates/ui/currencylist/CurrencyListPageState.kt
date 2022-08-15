package com.joshowen.forexexchangerates.ui.currencylist

import com.joshowen.repository.enums.CurrencyType

sealed class CurrencyListPageState {
    data class Success(val data: List<Pair<CurrencyType, String>>) : CurrencyListPageState()
    data class Error(val message: String) : CurrencyListPageState()
    object Loading : CurrencyListPageState()
}