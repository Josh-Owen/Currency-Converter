package com.joshowen.forexexchangerates.ui.currencyhistory

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.joshowen.forexexchangerates.databinding.HistoricListItemBinding
import com.joshowen.forexexchangerates.databinding.HistoricListItemHeaderBinding
import com.joshowen.forexexchangerates.ext.display
import com.joshowen.forexexchangerates.ext.roundToTwoDecimals
import com.joshowen.repository.data.CurrencyHistory

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
        return if(currentList.size <= 0) 0 else currentList.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is HeaderViewHolder -> {
                holder.bind(getItem(position))
            }
            is CurrencyHistoryViewHolder -> {
                holder.bind(getItem(
                    if((position - 1) > 0)
                        position - 1 else position)
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

        private val tvDate: TextView = binding.tvDateHeader
        private val tvEuros: TextView = binding.tvEurosHeader
        private val tvUSDollars: TextView = binding.tvUsDollarsHeader
        private val tvJapaneseYuan: TextView = binding.tvJapaneseYenHeader
        private val tvGreatBritishPounds: TextView = binding.tvBritishPoundsHeader
        private val tvAustralianDollar: TextView = binding.tvAustralianDollarsHeader
        private val tvCanadianDollars: TextView = binding.tvCanadianDollarsHeader
        private val tvSwissFranc: TextView = binding.tvSwissFrancHeader
        private val tvChineseYuan: TextView = binding.tvChineseYuanHeader
        private val tvSwedishKrona: TextView = binding.tvSwedishKronaHeader
        private val tvNewZealandDollar: TextView = binding.tvNewZealandDollarsHeader

        fun bind(priceInformation: CurrencyHistory) {

            tvDate.display()

            priceInformation.currencyPriceHistory.EUR?.let {
                tvEuros.display()
            }

            priceInformation.currencyPriceHistory.USD?.let {
                tvUSDollars.display()
            }

            priceInformation.currencyPriceHistory.JPY?.let {
                tvJapaneseYuan.display()
            }

            priceInformation.currencyPriceHistory.GBP?.let {
                tvGreatBritishPounds.display()
            }

            priceInformation.currencyPriceHistory.AUD?.let {
                tvAustralianDollar.display()
            }

            priceInformation.currencyPriceHistory.CAD?.let {
                tvCanadianDollars.display()
            }

            priceInformation.currencyPriceHistory.CHF?.let {
                tvSwissFranc.display()
            }

            priceInformation.currencyPriceHistory.CNY?.let {
                tvChineseYuan.display()
            }

            priceInformation.currencyPriceHistory.SEK?.let {
                tvSwedishKrona.display()
            }

            priceInformation.currencyPriceHistory.NZD?.let {
                tvNewZealandDollar.display()
            }
        }
    }
    //endregion

    //region CurrencyHistoryViewHolder
    inner class CurrencyHistoryViewHolder(binding: HistoricListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val tvDate: TextView = binding.tvDate
        private val tvEuros: TextView = binding.tvEuros
        private val tvUSDollars: TextView = binding.tvUsDollars
        private val tvJapaneseYuan: TextView = binding.tvJapaneseYen
        private val tvGreatBritishPounds: TextView = binding.tvBritishPounds
        private val tvAustralianDollar: TextView = binding.tvAustralianDollars
        private val tvCanadianDollars: TextView = binding.tvCanadianDollars
        private val tvSwissFranc: TextView = binding.tvSwissFranc
        private val tvChineseYuan: TextView = binding.tvChineseYuan
        private val tvSwedishKrona: TextView = binding.tvSwedishKrona
        private val tvNewZealandDollar: TextView = binding.tvNewZealandDollars

        fun bind(priceInformation: CurrencyHistory) {

            tvDate.text = priceInformation.date
            tvDate.display()

            priceInformation.currencyPriceHistory.EUR?.let {
                tvEuros.display()
                tvEuros.text = (specifiedAmount * it).roundToTwoDecimals()
            }

            priceInformation.currencyPriceHistory.USD?.let {
                tvUSDollars.display()
                tvUSDollars.text = (specifiedAmount * it).roundToTwoDecimals()
            }

            priceInformation.currencyPriceHistory.JPY?.let {
                tvJapaneseYuan.display()
                tvJapaneseYuan.text = (specifiedAmount * it).roundToTwoDecimals()
            }

            priceInformation.currencyPriceHistory.GBP?.let {
                tvGreatBritishPounds.display()
                tvGreatBritishPounds.text = (specifiedAmount * it).roundToTwoDecimals()
            }

            priceInformation.currencyPriceHistory.AUD?.let {
                tvAustralianDollar.display()
                tvAustralianDollar.text = (specifiedAmount * it).roundToTwoDecimals()
            }

            priceInformation.currencyPriceHistory.CAD?.let {
                tvCanadianDollars.display()
                tvCanadianDollars.text = (specifiedAmount * it).roundToTwoDecimals()
            }

            priceInformation.currencyPriceHistory.CHF?.let {
                tvSwissFranc.display()
                tvSwissFranc.text = (specifiedAmount * it).roundToTwoDecimals()
            }

            priceInformation.currencyPriceHistory.CNY?.let {
                tvChineseYuan.display()
                tvChineseYuan.text = (specifiedAmount * it).roundToTwoDecimals()
            }

            priceInformation.currencyPriceHistory.SEK?.let {
                tvSwedishKrona.display()
                tvSwedishKrona.text = (specifiedAmount * it).roundToTwoDecimals()
            }

            priceInformation.currencyPriceHistory.NZD?.let {
                tvNewZealandDollar.display()
                tvNewZealandDollar.text = (specifiedAmount * it).roundToTwoDecimals()
            }

        }
    }
    //endregion
}