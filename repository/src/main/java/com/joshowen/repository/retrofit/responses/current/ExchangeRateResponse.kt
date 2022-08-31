package com.joshowen.repository.retrofit.responses.current

import com.google.gson.annotations.SerializedName

internal data class ExchangeRateResponse (
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