package com.joshowen.forexexchangerates.repositories.fxexchange

import com.joshowen.forexexchangerates.data.Currency
import com.joshowen.forexexchangerates.data.CurrencyHistory
import com.joshowen.forexexchangerates.data.CurrencyType
import com.joshowen.forexexchangerates.retrofit.apis.ForeignExchangeAPI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ForeignExchangeRepositoryImpl @Inject internal constructor(private val foreignExchangeAPI: ForeignExchangeAPI, private val exchangeRateMapper : com.joshowen.forexexchangerates.mappers.ExchangeRateMapper, private val exchangeHistoryMapper : com.joshowen.forexexchangerates.mappers.ExchangeRateHistoricMapper) :
    ForeignExchangeRepository {

    //region ForeignExchangeRepository
    override fun getCurrencyInformation(
        baseCurrency: CurrencyType,
        supportedCurrencyCodes: String
    ): Flow<Result<List<Currency>>> {
        return flow {
            val apiResponse = foreignExchangeAPI.fetchLatestPricesForSymbols(
                supportedCurrencyCodes,
                baseCurrency.currencyCode
            )
            val mappedToRates = exchangeRateMapper.invoke(apiResponse.rates)
            emit(Result.success(mappedToRates))
        }.catch {
            emit(Result.failure(it))
        }
    }

    override fun getPriceHistory(
        baseCurrency: CurrencyType,
        selectedCurrencyCodes: String,
        startDate: LocalDate,
        endDate: LocalDate
    ) : Flow<Result<List<CurrencyHistory>>> {

        return flow {
            val apiResponse = foreignExchangeAPI.fetchHistoricPricesForSymbol(startDate, endDate, selectedCurrencyCodes, baseCurrency.currencyCode)
            val mappedExchangeRates = apiResponse.rates.map {
                exchangeHistoryMapper.invoke(it.toPair())
            }
            emit(Result.success(mappedExchangeRates))
        }.catch {
            emit(Result.failure(it))
        }
    }
    //endregion
}
