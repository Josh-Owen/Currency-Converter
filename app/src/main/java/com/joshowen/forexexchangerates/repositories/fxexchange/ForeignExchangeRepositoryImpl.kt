package com.joshowen.forexexchangerates.repositories.fxexchange

import com.joshowen.forexexchangerates.data.Currency
import com.joshowen.forexexchangerates.data.CurrencyHistory
import com.joshowen.forexexchangerates.data.CurrencyType
import com.joshowen.forexexchangerates.mappers.ExchangeRateHistoricMapper
import com.joshowen.forexexchangerates.mappers.ExchangeRateMapper
import com.joshowen.forexexchangerates.retrofit.apis.ForeignExchangeAPI
import com.joshowen.forexexchangerates.retrofit.wrappers.ApiError
import com.joshowen.forexexchangerates.retrofit.wrappers.ApiException
import com.joshowen.forexexchangerates.retrofit.wrappers.ApiResult
import com.joshowen.forexexchangerates.retrofit.wrappers.ApiSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ForeignExchangeRepositoryImpl @Inject constructor(
    private val foreignExchangeAPI: ForeignExchangeAPI,
    private val exchangeRateMapper: ExchangeRateMapper,
    private val exchangeHistoryMapper: ExchangeRateHistoricMapper
) : ForeignExchangeRepository {

    //region ForeignExchangeRepository
    override fun getCurrencyInformation(
        baseCurrency: CurrencyType,
        supportedCurrencyCodes: String
    ): Flow<ApiResult<List<Currency>>> {
        return flow {
            val apiResponse = foreignExchangeAPI.fetchLatestPricesForSymbols(
                supportedCurrencyCodes,
                baseCurrency.currencyCode
            )
            val apiResponseBody = apiResponse.body()

            if (apiResponse.isSuccessful && apiResponseBody != null) {
                val mappedToRates = exchangeRateMapper.invoke(apiResponseBody.rates)
                emit(ApiSuccess(mappedToRates))
            } else {
                emit(ApiError(code = apiResponse.code(), message = apiResponse.message()))
            }
        }.catch { e: Throwable ->
            emit(ApiException(e))
        }
    }

    override fun getPriceHistory(
        baseCurrency: CurrencyType,
        selectedCurrencyCodes: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<ApiResult<List<CurrencyHistory>>> {

        return flow {
            val apiResponse = foreignExchangeAPI.fetchHistoricPricesForSymbol(
                startDate,
                endDate,
                selectedCurrencyCodes,
                baseCurrency.currencyCode
            )
            val apiResponseBody = apiResponse.body()

            if (apiResponse.isSuccessful && apiResponseBody != null) {
                val mappedExchangeRates = apiResponseBody.rates.map {
                    exchangeHistoryMapper.invoke(it.toPair())
                }
                emit(ApiSuccess(mappedExchangeRates))
            } else {
                emit(ApiError(code = apiResponse.code(), message = apiResponse.message()))
            }
        }.catch { e: Throwable ->
            emit(ApiException(e))
        }
    }
    //endregion
}
