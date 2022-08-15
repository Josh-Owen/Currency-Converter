package com.joshowen.forexexchangerates.ui.currencylist

import androidx.lifecycle.*
import com.joshowen.forexexchangerates.base.DEFAULT_APPLICATION_CONVERSION_AMOUNT
import com.joshowen.forexexchangerates.base.DEFAULT_APP_CURRENCY
import com.joshowen.forexexchangerates.base.SUPPORTED_CURRENCIES
import com.joshowen.forexexchangerates.ext.roundToTwoDecimals
import com.joshowen.repository.data.Currency
import com.joshowen.repository.repository.ForeignExchangeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

//region CurrentListFragmentVMInputs
interface CurrentListFragmentVMInputs {
    fun setCurrencyAmount(amount : Int)
}
//endregion

//region CurrentListFragmentVMOutputs
interface CurrentListFragmentVMOutputs {
    fun fetchUiState() : LiveData<CurrencyListPageState>
    fun fetchDefaultApplicationCurrency() : LiveData<String>
    fun fetchSpecifiedAmount() : Flow<Int>
}
//endregion

@HiltViewModel
class CurrencyListFragmentVM @Inject constructor(private val foreignExchangeRepo: ForeignExchangeRepository): ViewModel(),CurrentListFragmentVMInputs, CurrentListFragmentVMOutputs {

    //region Variables & Class Members
    val inputs: CurrentListFragmentVMInputs = this
    val outputs: CurrentListFragmentVMOutputs = this

    val amountToConvert = MutableStateFlow(DEFAULT_APPLICATION_CONVERSION_AMOUNT)
    private val amountToConvertFlow = amountToConvert.asStateFlow()
    private val exchangeRates = MutableStateFlow<List<Currency>>(emptyList())
    private val exchangeRatesFlow = exchangeRates.asStateFlow()
    private val uiState = MutableLiveData<CurrencyListPageState>(null)

    //endregion

    init {

        viewModelScope.launch {
            fetchCurrencyInformation()
            amountToConvertFlow
                .combine(exchangeRatesFlow) { amountToConvert, apiResponse ->
                    val updatedValues = apiResponse.map { currentSymbol ->
                        Pair(
                            currentSymbol.currency,
                            (amountToConvert * (currentSymbol.price ?: 0.0))
                                .roundToTwoDecimals()
                        )
                    }
                    updatedValues
                }
                .onEach { uiState.value = CurrencyListPageState.Success(it) }
                .collect()
        }
    }

    //region Inputs
    override fun setCurrencyAmount(amount: Int) {
        amountToConvert.value = amount
    }
    //endregion

    //region Outputs
    override fun fetchUiState(): LiveData<CurrencyListPageState> {
        return uiState
    }

    override fun fetchDefaultApplicationCurrency(): LiveData<String> {
        return liveData {
            emit(DEFAULT_APP_CURRENCY.currencyCode)
        }
    }

    override fun fetchSpecifiedAmount(): Flow<Int> {
        return amountToConvertFlow
    }

    //endregion

    private suspend fun fetchCurrencyInformation() {
        if (uiState.value is CurrencyListPageState.Success) return

        uiState.value = CurrencyListPageState.Loading

        foreignExchangeRepo.getCurrencyInformation(DEFAULT_APP_CURRENCY, SUPPORTED_CURRENCIES)
            .collectLatest {
                try {
                    if (it.isSuccess) {
                        exchangeRates.value = it.getOrNull() ?: listOf()
                    } else {
                        uiState.value = CurrencyListPageState.Error("Something went wrong.")
                    }
                } catch (e: Exception) {
                    uiState.value =
                        CurrencyListPageState.Error(
                            e.message ?: ""
                        )
                }
            }
    }
}