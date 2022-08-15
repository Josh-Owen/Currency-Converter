package com.joshowen.forexexchangerates.ext

import java.text.DecimalFormat

fun Double.roundToTwoDecimals() : String {
    val df = DecimalFormat("    0.00")
    df.minimumFractionDigits = 2
    df.maximumFractionDigits = 2
    return "%.2f".format(this)
}