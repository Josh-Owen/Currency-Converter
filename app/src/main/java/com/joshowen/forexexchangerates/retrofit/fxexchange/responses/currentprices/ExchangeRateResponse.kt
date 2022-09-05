package com.joshowen.forexexchangerates.retrofit.fxexchange.responses.currentprices

import com.google.gson.annotations.SerializedName

data class ExchangeRateResponse (
    @SerializedName("base")
    val base: String?,
    @SerializedName("date")
    val date: String?,
    @SerializedName("rates")
    val rates : ExchangeRateItemRawResponse,
    @SerializedName("success")
    val success: Boolean?,
    @SerializedName("timestamp")
    val timestamp: Long?
)