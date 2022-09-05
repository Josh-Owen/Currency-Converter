package com.joshowen.forexexchangerates.ui.currencylist

import android.os.Bundle
import android.view.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.joshowen.forexexchangerates.R
import com.joshowen.forexexchangerates.base.BaseFragment
import com.joshowen.forexexchangerates.databinding.FragmentCurrencyListBinding
import com.joshowen.repository.enums.CurrencyType
import com.joshowen.forexexchangerates.ext.getSelectedItems
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CurrencyListFragment : BaseFragment<FragmentCurrencyListBinding>(), ActionMode.Callback {

    //region Variables & Class Members
    private val viewModel: CurrencyListFragmentVM by viewModels()

    private var tracker: SelectionTracker<Long>? = null

    private var actionMode: ActionMode? = null

    private val currencyAdapter: CurrencyListAdapter =
        CurrencyListAdapter(::navigateToCurrencyHistoryPage)


    //endregion

    //region Base Fragment Overrides / Life Cycle

    override fun inflateBinding(layoutInflater: LayoutInflater): FragmentCurrencyListBinding {
        return FragmentCurrencyListBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        super.initViews()



        binding.rvExchangeRates.apply {
            this.adapter = currencyAdapter
            this.layoutManager = LinearLayoutManager(requireContext())
        }

        binding.etAmount.addTextChangedListener {
            viewModel.inputs.setCurrencyAmount(it.toString().toIntOrNull() ?: 0)
        }

        tracker = SelectionTracker.Builder(
            CURRENCY_SELECTION_KEY,
            binding.rvExchangeRates,
            CurrencyKeyProvider(binding.rvExchangeRates),
            CurrencyDetailsLookup(binding.rvExchangeRates),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(

            object : SelectionTracker.SelectionPredicate<Long>() {
                override fun canSelectMultiple(): Boolean {
                    return true
                }

                override fun canSetStateForKey(key: Long, nextState: Boolean): Boolean {
                    if (nextState && (tracker?.selection?.size() ?: 0) >= MAX_SELECTIONS) {
                        tracker?.selection?.iterator()?.next()?.let {
                            tracker?.deselect(it)
                        }
                    }
                    return true
                }

                override fun canSetStateAtPosition(position: Int, nextState: Boolean): Boolean {
                    return true
                }
            }
        ).build()

        tracker?.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
            override fun onSelectionChanged() {

                if (actionMode == null) {
                    actionMode = activity?.startActionMode(this@CurrencyListFragment)
                }

                tracker?.let { tracker ->
                    val selectionSize = tracker.selection.size()
                    if (selectionSize == 0) {
                        actionMode?.finish()
                        tracker.clearSelection()
                        return
                    }
                    actionMode?.title = String.format(
                        getString(R.string.list_of_currencies_selected_amount_format),
                        selectionSize,
                        MAX_SELECTIONS
                    )
                }
            }
        })
        currencyAdapter.tracker = tracker
    }

    override fun observeViewModel() {

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.outputs.fetchSpecifiedAmount().collectLatest {
                    if (it != 0) {
                        binding.etAmount.setText(it.toString())
                        binding.etAmount.setSelection(binding.etAmount.text.length)
                    }
                }
            }
        }

        lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.outputs.fetchDefaultApplicationCurrency().collectLatest {
                    binding.tvDefaultCurrencyTitle.text = it
                }

                viewModel.outputs.fetchUiState().collectLatest {
                    binding.pbLoadCurrency.visibility =
                        if (it is CurrencyListPageState.Loading) View.VISIBLE else View.GONE
                    if (it is CurrencyListPageState.Success) {
                        currencyAdapter.submitList(it.data)
                    } else if (it is CurrencyListPageState.Error) {
                        Snackbar.make(
                            binding.root,
                            getString(R.string.generic_network_error),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    override fun initArgs(arguments: Bundle) {
        super.initArgs(arguments)
        tracker?.onRestoreInstanceState(arguments)
    }


    //endregion

    // region Navigation

    private fun navigateToCurrencyHistoryPage(selectedCurrencyType: Array<CurrencyType>) {
        val action =
            CurrencyListFragmentDirections.actionCurrencyListFragmentToCurrencyHistoryFragment(
                viewModel.amountToConvert.value.toString(),
                selectedCurrencyType
            )
        findNavController().navigate(action)
    }

    //endregion

    //region ActionMode.Callback
    override fun onCreateActionMode(actionMode: ActionMode, menu: Menu): Boolean {
        actionMode.menuInflater.inflate(R.menu.currency_selection_action_menu, menu)
        binding.etAmount.isEnabled = false
        return true
    }

    override fun onPrepareActionMode(p0: ActionMode?, p1: Menu?): Boolean {
        return true
    }


    override fun onActionItemClicked(p0: ActionMode?, item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.multipleCurrencies -> {

                val selected = currencyAdapter.currentList.getSelectedItems(
                    tracker?.selection?.toList() ?: listOf()
                ).map { it.currency }.toTypedArray()

                navigateToCurrencyHistoryPage(selected)

                tracker?.clearSelection()
                true
            }
            else -> false
        }
    }

    override fun onDestroyActionMode(actionModee: ActionMode) {
        this.actionMode = null
        actionMode?.finish()
        tracker?.clearSelection()
        binding.etAmount.isEnabled = true
    }

    //endregion

    //region Fragment Life-Cycle
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        tracker?.onSaveInstanceState(outState)
    }
    //endregion

    //region Companion Object
    companion object {
        const val CURRENCY_SELECTION_KEY = "CURRENCY_SELECTION_KEY"
        const val MAX_SELECTIONS = 3
    }
    //endregion
}


