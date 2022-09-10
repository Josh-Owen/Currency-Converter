package com.joshowen.forexexchangerates.data

data class CurrencyHistory(val date: String, var currencyPriceHistory: List<Currency>)