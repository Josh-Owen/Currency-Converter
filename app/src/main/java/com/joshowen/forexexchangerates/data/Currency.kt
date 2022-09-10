package com.joshowen.forexexchangerates.data

import kotlinx.serialization.Serializable

@Serializable
data class Currency(val currency: CurrencyType, var price: Double?)