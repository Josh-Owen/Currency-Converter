package com.joshowen.forexexchangerates.ui.currencyhistory

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.joshowen.forexexchangerates.R
import com.joshowen.forexexchangerates.base.BaseViewModel
import com.joshowen.forexexchangerates.base.DEFAULT_APP_CURRENCY
import com.joshowen.forexexchangerates.data.CurrencyType
import com.joshowen.forexexchangerates.dispatchers.DispatchersProvider
import com.joshowen.forexexchangerates.repositories.fxexchange.ForeignExchangeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import javax.inject.Inject

//region Interfaces
interface CurrencyHistoryFragmentVMInputs {
    fun setSupportedCurrencies(currencies: List<CurrencyType>)
    fun setSpecifiedCurrencyAmount(amount: String)
    fun setStartDate(startDate: LocalDate)
    fun setEndDateRange(endDate: LocalDate)
    suspend fun fetchPriceHistory()
}

interface CurrencyHistoryFragmentVMOutputs {
    fun fetchUiStateFlow(): Flow<CurrencyHistoryPageState>
    fun fetchSpecifiedCurrencyAmountFlow(): Flow<String>
    fun fetchUIState(): MutableStateFlow<CurrencyHistoryPageState>
}
//endregion

@HiltViewModel
class CurrencyHistoryFragmentVM @Inject constructor(
    application: Application,
    private val dispatchers: DispatchersProvider,
    private val foreignExchangeRepo: ForeignExchangeRepository
) : BaseViewModel(application), CurrencyHistoryFragmentVMInputs, CurrencyHistoryFragmentVMOutputs {

    //region Variables & Class Members
    val inputs: CurrencyHistoryFragmentVMInputs = this
    val outputs: CurrencyHistoryFragmentVMOutputs = this

    private val _userSelectedCurrencies = MutableStateFlow(emptyList<CurrencyType>())
    private val _userSpecifiedAmountOfCurrency = MutableStateFlow("")

    private val _historyStartDateRange = MutableStateFlow(LocalDate.now().minusDays(5))
    private val _historyEndDateRange = MutableStateFlow(LocalDate.now())

    private val _uiState =
        MutableStateFlow<CurrencyHistoryPageState>(CurrencyHistoryPageState.Loading)
    private val uiState: Flow<CurrencyHistoryPageState> = _uiState

    private val appConfigDefaultCurrency = flow {
        emit(DEFAULT_APP_CURRENCY.currencyCode)
    }.flowOn(dispatchers.io)

    //endregion


    //region CurrencyHistoryFragmentVMInputs
    override fun setSupportedCurrencies(currencies: List<CurrencyType>) {
        _userSelectedCurrencies.value = currencies
    }

    override fun setSpecifiedCurrencyAmount(amount: String) {
        _userSpecifiedAmountOfCurrency.value = amount
    }

    override suspend fun fetchPriceHistory() {

        viewModelScope.launch(dispatchers.io) {
            val selectedCurrencies =
                _userSelectedCurrencies.value.joinToString(",") { it.currencyCode }

            _uiState.value = CurrencyHistoryPageState.Loading

            foreignExchangeRepo.getPriceHistory(
                DEFAULT_APP_CURRENCY, selectedCurrencies,
                _historyStartDateRange.value, _historyEndDateRange.value
            )
                .flowOn(dispatchers.io)
                .collectLatest {
                    try {
                        if (it.isSuccess) {
                            _uiState.value =
                                CurrencyHistoryPageState.Success(it.getOrNull() ?: listOf())
                        } else {
                            _uiState.value =
                                CurrencyHistoryPageState.Error(
                                    getApplication<Application>().getString(
                                        R.string.generic_network_error
                                    )
                                )
                        }
                    } catch (exception: Exception) {
                        _uiState.value =
                            CurrencyHistoryPageState.Error(
                                exception.message.toString()
                            )
                    }
                }
        }
    }

    override fun setStartDate(startDate: LocalDate) {
        _historyStartDateRange.value = startDate
    }

    override fun setEndDateRange(endDate: LocalDate) {
        _historyEndDateRange.value = endDate
    }

    //endregion

    //region CurrencyHistoryFragmentVMOutputs
    override fun fetchUiStateFlow(): Flow<CurrencyHistoryPageState> {
        return uiState.flowOn(dispatchers.io)
    }

    override fun fetchSpecifiedCurrencyAmountFlow(): Flow<String> {
        return flow {
            emit("${appConfigDefaultCurrency.first()} ${_userSpecifiedAmountOfCurrency.value}")
        }.flowOn(dispatchers.io)
    }

    override fun fetchUIState(): MutableStateFlow<CurrencyHistoryPageState> {
        return _uiState
    }

    //endregion
}