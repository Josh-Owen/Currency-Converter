package com.joshowen.forexexchangerates.ui.currencylist

import com.joshowen.forexexchangerates.data.Currency

sealed class CurrencyListPageState {
    data class Success(val data: List<Currency>) : CurrencyListPageState()
    data class Error(val message: String) : CurrencyListPageState()
    object Idle  : CurrencyListPageState()
    object Loading : CurrencyListPageState()
}