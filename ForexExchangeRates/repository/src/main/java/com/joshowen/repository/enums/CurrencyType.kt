package com.joshowen.repository.enums

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class CurrencyType(val currencyCode : String) :Parcelable{
    EUROS("EUR"),
    US_DOLLARS("USD"),
    JAPANESE_YEN("JPY"),
    GREAT_BRITISH_POUNDS("GBP"),
    AUSTRALIAN_DOLLARS("AUD"),
    CANADIAN_DOLLARS("CAD"),
    SWISS_FRANC("CHF"),
    CHINESE_YUAN("CNY"),
    SWEDISH_KRONA("SEK"),
    NEW_ZEALAND_DOLLARS("NZD")
}