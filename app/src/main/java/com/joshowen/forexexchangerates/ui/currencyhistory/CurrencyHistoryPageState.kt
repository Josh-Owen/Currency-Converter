package com.joshowen.forexexchangerates.ui.currencyhistory

import com.joshowen.forexexchangerates.data.CurrencyHistory

sealed class CurrencyHistoryPageState() {
    data class Success(val data: List<CurrencyHistory>) : CurrencyHistoryPageState()
    data class Error(val message: String) : CurrencyHistoryPageState()
    object Loading : CurrencyHistoryPageState()
}