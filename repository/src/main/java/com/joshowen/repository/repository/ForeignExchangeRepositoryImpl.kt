package com.joshowen.repository.repository

import com.joshowen.repository.data.Currency
import com.joshowen.repository.data.CurrencyHistory
import com.joshowen.repository.enums.CurrencyType
import com.joshowen.repository.mappers.ExchangeRateHistoricMapper
import com.joshowen.repository.mappers.ExchangeRateMapper
import com.joshowen.repository.retrofit.ForeignExchangeAPI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ForeignExchangeRepositoryImpl @Inject internal constructor(private val foreignExchangeAPI: ForeignExchangeAPI, private val exchangeRateMapper : ExchangeRateMapper, private val exchangeHistoryMapper : ExchangeRateHistoricMapper) : ForeignExchangeRepository {

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

    override fun getPricedHistory(
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
