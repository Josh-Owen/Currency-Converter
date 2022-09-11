package com.joshowen.forexexchangerates.ui.currencylist

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.joshowen.forexexchangerates.R
import com.joshowen.forexexchangerates.base.BaseViewModel
import com.joshowen.forexexchangerates.base.DEFAULT_APPLICATION_CONVERSION_AMOUNT
import com.joshowen.forexexchangerates.base.DEFAULT_APP_CURRENCY
import com.joshowen.forexexchangerates.base.SUPPORTED_CURRENCIES
import com.joshowen.forexexchangerates.data.Currency
import com.joshowen.forexexchangerates.dispatchers.DispatchersProvider
import com.joshowen.forexexchangerates.repositories.fxexchange.ForeignExchangeRepository
import com.joshowen.forexexchangerates.retrofit.apis.FX_API_ERROR_CODE_API_LIMIT_EXCEEDED
import com.joshowen.forexexchangerates.retrofit.wrappers.ApiError
import com.joshowen.forexexchangerates.retrofit.wrappers.ApiSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

//region CurrentListFragmentVMInputs
interface CurrentListFragmentVMInputs {
    fun setCurrencyAmount(amount: Int)
    suspend fun fetchCurrencyInformation()
}
//endregion

//region CurrentListFragmentVMOutputs
interface CurrentListFragmentVMOutputs {
    fun fetchUiState(): Flow<CurrencyListPageState>
    fun fetchSpecifiedAmountOfCurrencyFlow(): Flow<Int>
    fun fetchUIStateFlow(): MutableStateFlow<CurrencyListPageState>
    fun fetchDefaultApplicationCurrencyFlow(): Flow<String>
    fun fetchSpecifiedAmountOfCurrency(): Int
}
//endregion

@HiltViewModel
class CurrencyListFragmentVM @Inject constructor(
    application: Application,
    private val dispatchers: DispatchersProvider,
    private val foreignExchangeRepo: ForeignExchangeRepository
) : BaseViewModel(application), CurrentListFragmentVMInputs, CurrentListFragmentVMOutputs {

    //region Variables & Class Members
    val inputs: CurrentListFragmentVMInputs = this
    val outputs: CurrentListFragmentVMOutputs = this

    private val _specifiedConversionAmount = MutableStateFlow(DEFAULT_APPLICATION_CONVERSION_AMOUNT)
    private val _exchangeRates = MutableStateFlow<List<Currency>>(emptyList())
    private val _uiState = MutableStateFlow<CurrencyListPageState>(CurrencyListPageState.Loading)
    private val uiState: Flow<CurrencyListPageState> = _uiState

    //endregion

    init {

        viewModelScope.launch(dispatchers.io) {
            fetchCurrencyInformation()
            _specifiedConversionAmount
                .combine(_exchangeRates) { amountToConvert, selectedCurrenciesExchangeRates ->
                    val updatedValues = selectedCurrenciesExchangeRates.map { currentSymbol ->
                        Currency(
                            currentSymbol.currency,
                            (amountToConvert * (currentSymbol.price ?: 0.0))
                        )
                    }
                    updatedValues
                }
                .flowOn(dispatchers.io)
                .filter { it.isNotEmpty() }
                .onEach { _uiState.value = CurrencyListPageState.Success(it) }
                .collect()
        }
    }

    //region Inputs
    override fun setCurrencyAmount(amount: Int) {
        _specifiedConversionAmount.value = amount
    }

    override suspend fun fetchCurrencyInformation() {
        _uiState.value = CurrencyListPageState.Loading
        viewModelScope.launch(dispatchers.io) {

            foreignExchangeRepo.getCurrencyInformation(DEFAULT_APP_CURRENCY, SUPPORTED_CURRENCIES)
                .flowOn(dispatchers.io)
                .catch {
                    _uiState.value =
                        CurrencyListPageState.Error(
                            it.message.toString()
                        )
                }
                .collectLatest {
                    if (it is ApiSuccess) {
                        _exchangeRates.value = it.data
                    } else if (it is ApiError) {
                        if (it.code == FX_API_ERROR_CODE_API_LIMIT_EXCEEDED) {
                            _uiState.value = CurrencyListPageState.Error(
                                getApplication<Application>().getString(
                                    R.string.network_error_api_call_limit
                                )
                            )
                        } else {
                            _uiState.value = CurrencyListPageState.Error(
                                getApplication<Application>().getString(
                                    R.string.generic_network_error
                                )
                            )
                        }
                    } else {
                        _uiState.value = CurrencyListPageState.Error(
                            getApplication<Application>().getString(
                                R.string.generic_network_error
                            )
                        )
                    }
                }
        }
    }

    //endregion

    //region Outputs
    override fun fetchUiState(): Flow<CurrencyListPageState> {
        return uiState.flowOn(dispatchers.io)
    }

    override fun fetchDefaultApplicationCurrencyFlow(): Flow<String> {
        return flow {
            emit(DEFAULT_APP_CURRENCY.currencyCode)
        }.flowOn(dispatchers.io)
    }

    override fun fetchSpecifiedAmountOfCurrencyFlow(): Flow<Int> {
        return _specifiedConversionAmount
    }

    override fun fetchUIStateFlow(): MutableStateFlow<CurrencyListPageState> {
        return _uiState
    }

    override fun fetchSpecifiedAmountOfCurrency(): Int {
        return _specifiedConversionAmount.value
    }

    //endregion
}