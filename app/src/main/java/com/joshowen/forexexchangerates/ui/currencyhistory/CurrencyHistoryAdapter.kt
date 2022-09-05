package com.joshowen.forexexchangerates.ui.currencyhistory

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.joshowen.forexexchangerates.R
import com.joshowen.forexexchangerates.databinding.HistoricListItemBinding
import com.joshowen.forexexchangerates.databinding.HistoricListItemHeaderBinding
import com.joshowen.forexexchangerates.ext.display
import com.joshowen.forexexchangerates.ext.roundToTwoDecimalPlaces
import com.joshowen.repository.data.CurrencyHistory
import com.joshowen.repository.enums.CurrencyType

class CurrencyHistoryAdapter(val specifiedAmount : Double) : ListAdapter<CurrencyHistory, RecyclerView.ViewHolder>(CurrencyComparator()) {

    //region Companion Object
    companion object {
        private const val TYPE_HEADER_ITEM: Int = 0
        private const val TYPE_LIST_ITEM: Int = 1
    }
    //endregion

    //region ListAdapter Overrides
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when(viewType) {
            TYPE_HEADER_ITEM -> {
                HeaderViewHolder(
                    HistoricListItemHeaderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                CurrencyHistoryViewHolder(
                    HistoricListItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(position) {
            0 -> TYPE_HEADER_ITEM
            else -> TYPE_LIST_ITEM
        }
    }

    override fun getItemCount(): Int {
        return if (currentList.size == 0)
            0
        else
            currentList.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is HeaderViewHolder -> {
                holder.bind(getItem(position))
            }
            is CurrencyHistoryViewHolder -> {
                holder.bind(
                    getItem(
                        if(position - 1 <= 0)
                            0
                                    else
                        position - 1

                    )
                )
            }
        }
    }
    //endregion

    //region DiffUtil.ItemCallback
    class CurrencyComparator : DiffUtil.ItemCallback<CurrencyHistory>() {
        override fun areItemsTheSame(oldItem: CurrencyHistory, newItem : CurrencyHistory) =
            oldItem.date == newItem.date

        override fun areContentsTheSame(oldItem: CurrencyHistory, newItem : CurrencyHistory) =
            oldItem == newItem
    }
    //endregion

    //region HeaderViewHolder
    inner class HeaderViewHolder(binding: HistoricListItemHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val tvFirstCurrencyHeader: TextView = binding.tvFirstCurrencyHeader
        private val tvSecondCurrencyHeader: TextView = binding.tvSecondCurrencyHeader
        private val tvThirdCurrencyHeader: TextView = binding.tvThirdCurrencyHeader

        fun bind(priceInformation: CurrencyHistory) {

            priceInformation.currencyPriceHistory.find { it.currency == CurrencyType.EUROS }?.price?.let {
                initialiseAndDisplayCorrespondingHeader(R.string.currency_codes_eur)
            }

            priceInformation.currencyPriceHistory.find { it.currency == CurrencyType.US_DOLLARS }?.price?.let {
                initialiseAndDisplayCorrespondingHeader(R.string.currency_codes_usd)
            }

            priceInformation.currencyPriceHistory.find { it.currency == CurrencyType.JAPANESE_YEN }?.price?.let {
                initialiseAndDisplayCorrespondingHeader(R.string.currency_codes_jpy)
            }

            priceInformation.currencyPriceHistory.find { it.currency == CurrencyType.GREAT_BRITISH_POUNDS }?.price?.let {
                initialiseAndDisplayCorrespondingHeader(R.string.currency_codes_gbp)
            }

            priceInformation.currencyPriceHistory.find { it.currency == CurrencyType.AUSTRALIAN_DOLLARS }?.price?.let {
                initialiseAndDisplayCorrespondingHeader(R.string.currency_codes_aud)
            }

            priceInformation.currencyPriceHistory.find { it.currency == CurrencyType.CANADIAN_DOLLARS }?.price?.let {
                initialiseAndDisplayCorrespondingHeader(R.string.currency_codes_cad)
            }

            priceInformation.currencyPriceHistory.find { it.currency == CurrencyType.SWISS_FRANC }?.price?.let {
                initialiseAndDisplayCorrespondingHeader(R.string.currency_codes_chf)
            }

            priceInformation.currencyPriceHistory.find { it.currency == CurrencyType.CHINESE_YUAN }?.price?.let {
                initialiseAndDisplayCorrespondingHeader(R.string.currency_codes_cny)
            }

            priceInformation.currencyPriceHistory.find { it.currency == CurrencyType.SWEDISH_KRONA }?.price?.let {
                initialiseAndDisplayCorrespondingHeader(R.string.currency_codes_sek)
            }

            priceInformation.currencyPriceHistory.find { it.currency == CurrencyType.NEW_ZEALAND_DOLLARS }?.price?.let {
                initialiseAndDisplayCorrespondingHeader(R.string.currency_codes_nzd)
            }
        }

        private fun initialiseAndDisplayCorrespondingHeader(titleResourceId : Int) {

            if (!tvFirstCurrencyHeader.isVisible) {
                tvFirstCurrencyHeader.setText(titleResourceId)
                tvFirstCurrencyHeader.display()
            } else if (!tvSecondCurrencyHeader.isVisible) {
                tvSecondCurrencyHeader.setText(titleResourceId)
                tvSecondCurrencyHeader.display()
            } else if (!tvThirdCurrencyHeader.isVisible) {
                tvThirdCurrencyHeader.setText(titleResourceId)
                tvThirdCurrencyHeader.display()
            } else {
                return
            }
        }
    }
    //endregion

    //region CurrencyHistoryViewHolder
    inner class CurrencyHistoryViewHolder(binding: HistoricListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val tvDate: TextView = binding.tvDate
        private val tvFirstCurrency: TextView = binding.tvFirstCurrency
        private val tvSecondCurrency: TextView = binding.tvSecondCurrency
        private val tvThirdCurrency: TextView = binding.tvThirdCurrency

        fun bind(priceInformation: CurrencyHistory) {

            tvDate.text = priceInformation.date

            priceInformation.currencyPriceHistory.find { it.currency == CurrencyType.EUROS }?.price?.let {
                initialiseAndDisplayCorrespondingCurrency((specifiedAmount * it).roundToTwoDecimalPlaces())
            }

            priceInformation.currencyPriceHistory.find { it.currency == CurrencyType.US_DOLLARS }?.price?.let {
                initialiseAndDisplayCorrespondingCurrency((specifiedAmount * it).roundToTwoDecimalPlaces())
            }

            priceInformation.currencyPriceHistory.find { it.currency == CurrencyType.JAPANESE_YEN }?.price?.let {
                initialiseAndDisplayCorrespondingCurrency((specifiedAmount * it).roundToTwoDecimalPlaces())
            }

            priceInformation.currencyPriceHistory.find { it.currency == CurrencyType.GREAT_BRITISH_POUNDS }?.price?.let {
                initialiseAndDisplayCorrespondingCurrency((specifiedAmount * it).roundToTwoDecimalPlaces())
            }

            priceInformation.currencyPriceHistory.find { it.currency == CurrencyType.AUSTRALIAN_DOLLARS }?.price?.let {
                initialiseAndDisplayCorrespondingCurrency((specifiedAmount * it).roundToTwoDecimalPlaces())
            }

            priceInformation.currencyPriceHistory.find { it.currency == CurrencyType.CANADIAN_DOLLARS }?.price?.let {
                initialiseAndDisplayCorrespondingCurrency((specifiedAmount * it).roundToTwoDecimalPlaces())
            }

            priceInformation.currencyPriceHistory.find { it.currency == CurrencyType.SWISS_FRANC }?.price?.let {
                initialiseAndDisplayCorrespondingCurrency((specifiedAmount * it).roundToTwoDecimalPlaces())
            }

            priceInformation.currencyPriceHistory.find { it.currency == CurrencyType.CHINESE_YUAN }?.price?.let {
                initialiseAndDisplayCorrespondingCurrency((specifiedAmount * it).roundToTwoDecimalPlaces())
            }

            priceInformation.currencyPriceHistory.find { it.currency == CurrencyType.SWEDISH_KRONA }?.price?.let {
                initialiseAndDisplayCorrespondingCurrency((specifiedAmount * it).roundToTwoDecimalPlaces())
            }

            priceInformation.currencyPriceHistory.find { it.currency == CurrencyType.NEW_ZEALAND_DOLLARS }?.price?.let {
                initialiseAndDisplayCorrespondingCurrency((specifiedAmount * it).roundToTwoDecimalPlaces())
            }
        }

        private fun initialiseAndDisplayCorrespondingCurrency(amount : String) {

            if (!tvFirstCurrency.isVisible) {
                tvFirstCurrency.text = amount
                tvFirstCurrency.display()
            } else if (!tvSecondCurrency.isVisible) {
                tvSecondCurrency.text = amount
                tvSecondCurrency.display()
            } else if (!tvThirdCurrency.isVisible) {
                tvThirdCurrency.text = amount
                tvThirdCurrency.display()
            } else {
                return
            }
        }
    }
    //endregion
}