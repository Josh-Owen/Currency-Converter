package com.joshowen.repository.retrofit.responses.historic

import com.google.gson.annotations.SerializedName
import com.joshowen.repository.retrofit.responses.current.ExchangeRateItemRawResponse
import java.util.*

data class FetchHistoricTimeSeriesResult (
    @SerializedName("base")
    val base: String?,
    @SerializedName("date")
    val date: String?,
    @SerializedName("rates")
    val rates: Map<String, ExchangeRateItemRawResponse> = emptyMap(),
    @SerializedName("success")
    val success: Boolean?,
    @SerializedName("timestamp")
    val timestamp: Long?,
    @SerializedName("timeseries")
    val timeseries : Boolean?= null,
    @SerializedName("start_date")
    val start_dart : Date?,
    @SerializedName("end_date")
    val end_date : Date?,
)