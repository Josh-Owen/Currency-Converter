package com.joshowen.forexexchangerates.ui.currencyhistory

import androidx.lifecycle.*
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
    fun fetchUiState() : LiveData<CurrencyHistoryPageState>
    fun fetchSpecifiedCurrencyAmount() : LiveData<String>
}
//endregion

@HiltViewModel
class CurrencyHistoryFragmentVM @Inject constructor(private val foreignExchangeRepo : ForeignExchangeRepository): ViewModel(),CurrencyHistoryFragmentVMInputs, CurrencyHistoryFragmentVMOutputs {

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

    private val uiState = MutableLiveData<CurrencyHistoryPageState>()

    //endregion

    //region CurrencyHistoryFragmentVMInputs
    override fun setSupportedCurrencies(currencies: List<CurrencyType>) {
        userSelectedCurrencies.value = currencies
    }

    override fun setSpecifiedCurrencyAmount(amount: String) {
        userSpecifiedAmountOfCurrency.value = amount
    }

    override fun fetchPriceHistory() {
        if (uiState.value is CurrencyHistoryPageState.Success) return
        viewModelScope.launch {
            uiState.value = CurrencyHistoryPageState.Loading
            val selectedCurrencies =
                userSelectedCurrencies.value.joinToString(",") { it.currencyCode }
            foreignExchangeRepo.getPricedHistory(
                DEFAULT_APP_CURRENCY, selectedCurrencies,
                historyStartDate.value, historyEndDate.value
            ).collect {
                if (it.isSuccess) {
                    uiState.value = CurrencyHistoryPageState.Success(it.getOrNull() ?: listOf())
                } else if (it.isFailure) {
                    uiState.value =
                        CurrencyHistoryPageState.Error(it.exceptionOrNull()?.message ?: "")
                }
            }
        }
    }

    //endregion

    //region CurrencyHistoryFragmentVMOutputs
    override fun fetchUiState(): LiveData<CurrencyHistoryPageState> {
        return uiState
    }

    override fun fetchSpecifiedCurrencyAmount(): LiveData<String> {
        return liveData {
            emit("${appConfigDefaultCurrency.first()} ${userSpecifiedAmountOfCurrency.value}")
        }
    }
    //endregion

}