package com.joshowen.forexexchangerates.ui.currencyhistory

import android.app.Application
import androidx.lifecycle.*
import com.joshowen.forexexchangerates.R
import com.joshowen.forexexchangerates.base.BaseViewModel
import com.joshowen.forexexchangerates.base.DEFAULT_APP_CURRENCY
import com.joshowen.repository.enums.CurrencyType
import com.joshowen.repository.repository.ForeignExchangeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import javax.inject.Inject

//region Interfaces
interface CurrencyHistoryFragmentVMInputs {
    fun setSupportedCurrencies(currencies : List<CurrencyType>)
    fun setSpecifiedCurrencyAmount(amount : String)
    fun fetchPriceHistory()
}

interface CurrencyHistoryFragmentVMOutputs {
    fun fetchUiState() : Flow<CurrencyHistoryPageState>
    fun fetchSpecifiedCurrencyAmount() : Flow<String>
}
//endregion

@HiltViewModel
class CurrencyHistoryFragmentVM @Inject constructor(application: Application, private val foreignExchangeRepo : ForeignExchangeRepository): BaseViewModel(application),CurrencyHistoryFragmentVMInputs, CurrencyHistoryFragmentVMOutputs {

    //region Variables & Class Members
    val inputs: CurrencyHistoryFragmentVMInputs = this
    val outputs: CurrencyHistoryFragmentVMOutputs = this

    private val userSelectedCurrencies = MutableStateFlow(emptyList<CurrencyType>())
    private val userSpecifiedAmountOfCurrency = MutableLiveData("")

    private val appConfigDefaultCurrency = flow {
        emit(DEFAULT_APP_CURRENCY.currencyCode)
    }

    private val historyStartDate = MutableStateFlow(LocalDate.now().minusDays(5))
    private val historyEndDate = MutableStateFlow(LocalDate.now())

    private val _uiState =
        MutableStateFlow<CurrencyHistoryPageState>(CurrencyHistoryPageState.Success(emptyList()))
    private val uiState: Flow<CurrencyHistoryPageState> = _uiState

    //endregion

    //region CurrencyHistoryFragmentVMInputs
    override fun setSupportedCurrencies(currencies: List<CurrencyType>) {
        userSelectedCurrencies.value = currencies
    }

    override fun setSpecifiedCurrencyAmount(amount: String) {
        userSpecifiedAmountOfCurrency.value = amount
    }

    override fun fetchPriceHistory() {
        viewModelScope.launch {
            _uiState.value = CurrencyHistoryPageState.Loading
            val selectedCurrencies =
                userSelectedCurrencies.value.joinToString(",") { it.currencyCode }
            foreignExchangeRepo.getPricedHistory(
                DEFAULT_APP_CURRENCY, selectedCurrencies,
                historyStartDate.value, historyEndDate.value
            ).collect {
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
                            exception.message ?: getApplication<Application>().getString(
                                R.string.generic_network_error
                            )
                        )
                }
            }
        }
    }

    //endregion

    //region CurrencyHistoryFragmentVMOutputs
    override fun fetchUiState(): Flow<CurrencyHistoryPageState> {
        return uiState
    }

    override fun fetchSpecifiedCurrencyAmount(): Flow<String> {
        return flow {
            emit("${appConfigDefaultCurrency.first()} ${userSpecifiedAmountOfCurrency.value}")
        }
    }
    //endregion
}