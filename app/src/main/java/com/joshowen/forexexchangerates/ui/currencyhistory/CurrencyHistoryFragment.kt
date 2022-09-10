package com.joshowen.forexexchangerates.ui.currencyhistory

import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.joshowen.forexexchangerates.base.BaseFragment
import com.joshowen.forexexchangerates.databinding.FragmentCurrencyHistoryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate

@AndroidEntryPoint
class CurrencyHistoryFragment : BaseFragment<FragmentCurrencyHistoryBinding>() {

    //region Variables & Class Members
    private val viewModel: CurrencyHistoryFragmentVM by viewModels()
    private val navArgs by navArgs<CurrencyHistoryFragmentArgs>()
    private lateinit var currencyHistoryAdapter: CurrencyHistoryAdapter

    //endregion

    //region BaseFragment Overrides
    override fun inflateBinding(layoutInflater: LayoutInflater): FragmentCurrencyHistoryBinding {
        return FragmentCurrencyHistoryBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        super.initViews()
        currencyHistoryAdapter =
            CurrencyHistoryAdapter(navArgs.specifiedAmountOfCurrency.toDouble())
        binding.rvHistoricPrices.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = currencyHistoryAdapter
        }
    }

    override fun observeViewModel() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewModel.inputs.setSupportedCurrencies(navArgs.selectedCurrencies.toList())
            viewModel.inputs.setSpecifiedCurrencyAmount(navArgs.specifiedAmountOfCurrency)
            viewModel.inputs.setStartDate(LocalDate.now().minusDays(5))
            viewModel.inputs.setEndDateRange(LocalDate.now())

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.outputs.fetchSpecifiedCurrencyAmountFlow().collectLatest {
                    binding.tvCurrencyAndAmount.text = it
                }

                viewModel.inputs.fetchPriceHistory()


                viewModel.outputs.fetchUiStateFlow().collectLatest { state ->

                    binding.pbLoadingPriceHistory.visibility =
                        if (state is CurrencyHistoryPageState.Loading) View.VISIBLE else View.GONE

                    binding.btnRetryLoadPriceHistory.visibility =
                        if (state is CurrencyHistoryPageState.Error) View.VISIBLE else View.GONE

                    when (state) {
                        is CurrencyHistoryPageState.Success -> {
                            currencyHistoryAdapter.submitList(state.data)
                        }
                        is CurrencyHistoryPageState.Error -> {
                            Snackbar.make(
                                binding.root,
                                state.message,
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                        else -> {}
                    }
                }
            }
        }
    }
    //endregion
}