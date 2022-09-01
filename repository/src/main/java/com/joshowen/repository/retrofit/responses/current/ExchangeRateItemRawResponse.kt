package com.joshowen.repository.retrofit.responses.current

import com.google.gson.annotations.SerializedName

internal class ExchangeRateItemRawResponse(
    @SerializedName("USD")
    val USD : Double?,
    @SerializedName("EUR")
    val EUR : Double?,
    @SerializedName("JPY")
    val JPY : Double?,
    @SerializedName("GBP")
    val GBP : Double?,
    @SerializedName("AUD")
    val AUD: Double?,
    @SerializedName("CAD")
    val CAD : Double?,
    @SerializedName("CHF")
    val CHF : Double?,
    @SerializedName("CNY")
    val CNY : Double?,
    @SerializedName("SEK")
    val SEK : Double?,
    @SerializedName("NZD")
    val NZD: Double?
)