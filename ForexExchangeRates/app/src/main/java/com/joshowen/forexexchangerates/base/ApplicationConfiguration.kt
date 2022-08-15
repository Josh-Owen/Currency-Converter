package com.joshowen.forexexchangerates.base

import com.joshowen.repository.enums.CurrencyType

val DEFAULT_APP_CURRENCY : CurrencyType = CurrencyType.EUROS

val SUPPORTED_CURRENCIES = listOf(
    CurrencyType.EUROS.currencyCode,
    CurrencyType.US_DOLLARS.currencyCode,
    CurrencyType.JAPANESE_YEN.currencyCode,
    CurrencyType.GREAT_BRITISH_POUNDS.currencyCode,
    CurrencyType.AUSTRALIAN_DOLLARS.currencyCode,
    CurrencyType.CANADIAN_DOLLARS.currencyCode,
    CurrencyType.SWISS_FRANC.currencyCode,
    CurrencyType.CHINESE_YUAN.currencyCode,
    CurrencyType.SWEDISH_KRONA.currencyCode,
    CurrencyType.NEW_ZEALAND_DOLLARS.currencyCode
).joinToString()

val DEFAULT_APPLICATION_CONVERSION_AMOUNT = 100


