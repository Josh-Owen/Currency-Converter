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
import com.joshowen.forexexchangerates.R
import com.joshowen.forexexchangerates.base.BaseFragment
import com.joshowen.forexexchangerates.databinding.FragmentCurrencyHistoryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
        currencyHistoryAdapter = CurrencyHistoryAdapter(navArgs.specifiedAmountOfCurrency.toDouble())
        binding.rvHistoricPrices.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = currencyHistoryAdapter
        }
    }

    override fun observeViewModel() {

        lifecycleScope.launch {

            viewModel.inputs.setSupportedCurrencies(navArgs.selectedCurrencies.toList())
            viewModel.inputs.setSpecifiedCurrencyAmount(navArgs.specifiedAmountOfCurrency)

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.fetchPriceHistory()

                viewModel.outputs.fetchSpecifiedCurrencyAmount().observe(viewLifecycleOwner) {
                    binding.tvCurrencyAndAmount.text = it.toString()
                }

                viewModel.fetchUiState().observe(viewLifecycleOwner) {

                    binding.pbLoadingPriceHistory.visibility =
                        if (it is CurrencyHistoryPageState.Loading) View.VISIBLE else View.GONE

                    when (it) {
                        is CurrencyHistoryPageState.Success -> {
                            currencyHistoryAdapter.submitList(it.data)
                        }
                        is CurrencyHistoryPageState.Error -> {
                            Snackbar.make(
                                binding.root,
                                getString(R.string.generic_network_error),
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                        else -> {

                        }
                    }
                }
            }
        }
    }
    //endregion

}