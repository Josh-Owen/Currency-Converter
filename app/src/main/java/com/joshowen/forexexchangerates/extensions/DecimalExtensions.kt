package com.joshowen.forexexchangerates.extensions

import java.text.DecimalFormat

fun Double.roundToTwoDecimalPlaces() : String {
    val df = DecimalFormat("0.00")
    df.minimumFractionDigits = 2
    df.maximumFractionDigits = 2
    return "%.2f".format(this)
}