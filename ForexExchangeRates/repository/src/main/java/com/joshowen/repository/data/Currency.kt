package com.joshowen.repository.data

import com.joshowen.repository.enums.CurrencyType
import kotlinx.serialization.Serializable

@Serializable
data class Currency(val currency : CurrencyType, var price : Double?)