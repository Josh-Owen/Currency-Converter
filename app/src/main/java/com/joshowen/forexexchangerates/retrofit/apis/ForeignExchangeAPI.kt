package com.joshowen.forexexchangerates.retrofit.apis

import com.joshowen.forexexchangerates.retrofit.fxexchange.responses.currentprices.ExchangeRateResponse
import com.joshowen.forexexchangerates.retrofit.fxexchange.responses.historicprices.FetchHistoricTimeSeriesResult
import org.threeten.bp.LocalDate
import retrofit2.http.GET
import retrofit2.http.Query

interface ForeignExchangeAPI {

    @GET("latest")
    suspend fun fetchLatestPricesForSymbols(
        @Query("symbols") supportedCurrencyCodes: String,
        @Query("base") baseCurrencyCode: String
    ): ExchangeRateResponse

    @GET("timeseries")
    suspend fun fetchHistoricPricesForSymbol(
        @Query("start_date") startDate: LocalDate,
        @Query("end_date") endDate: LocalDate,
        @Query("symbols") currencyCodes: String,
        @Query("base") baseCurrencyCode: String
    ): FetchHistoricTimeSeriesResult

}