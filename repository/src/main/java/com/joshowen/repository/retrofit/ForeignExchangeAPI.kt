package com.joshowen.repository.retrofit

import com.joshowen.repository.retrofit.result.ExchangeRateResponse
import com.joshowen.repository.retrofit.result.FetchHistoricTimeSeriesResult
import org.threeten.bp.LocalDate
import retrofit2.http.GET
import retrofit2.http.Query

internal interface ForeignExchangeAPI {

    @GET("latest")
    suspend fun fetchLatestPricesForSymbols(@Query("symbols") supportedCurrencyCodes : String, @Query("base") baseCurrencyCode : String) : ExchangeRateResponse

    @GET("timeseries")
    suspend fun fetchHistoricPricesForSymbol(@Query("start_date") startDate : LocalDate, @Query("end_date") endDate : LocalDate, @Query("symbols") currencyCodes : String, @Query("base") baseCurrencyCode : String) : FetchHistoricTimeSeriesResult

}