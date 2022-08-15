package com.joshowen.forexexchangerates.ui.currencylist

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.joshowen.forexexchangerates.databinding.CurrencyListItemBinding
import com.joshowen.repository.enums.CurrencyType

//region CurrencyListAdapter
class CurrencyListAdapter(
    private val listener : (Array<CurrencyType>) -> Unit
) : ListAdapter<Pair<CurrencyType, String>, CurrencyListAdapter.CurrencyViewHolder>(CurrencyComparator()) {

    //region Variables & Class Members
    var tracker: SelectionTracker<Long>? = null

    //endregion

    init {
        setHasStableIds(true)
    }

    //region ListAdapter Overrides
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {

        return CurrencyViewHolder(
            CurrencyListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemId(position: Int): Long = position.toLong()

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        tracker?.let {
            holder.bind(getItem(position), it.isSelected(position.toLong()))
        }
    }

    //endregion

    //region DiffUtil.ItemCallback
    class CurrencyComparator : DiffUtil.ItemCallback<Pair<CurrencyType, String>>() {
        override fun areItemsTheSame(
            oldItem: Pair<CurrencyType, String>,
            newItem: Pair<CurrencyType, String>
        ) =
            oldItem.first == newItem.first

        override fun areContentsTheSame(
            oldItem: Pair<CurrencyType, String>,
            newItem: Pair<CurrencyType, String>
        ) =
            oldItem == newItem
    }
    //endregion

    //region CurrencyViewHolder
    inner class CurrencyViewHolder(binding: CurrencyListItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        //region Variables & Class Members
        private val tvName: TextView = binding.tvCurrencyName
        private val tvCategory: TextView = binding.tvExchangeValue
        private val clParent: ConstraintLayout = binding.root
        private val cbIsSelected: CheckBox = binding.cbIsSelected
        private var currencyType: CurrencyType? = null
        //endregion

        fun bind(priceInformation: Pair<CurrencyType, String>, isSelected: Boolean) {
            this.currencyType = priceInformation.first
            itemView.isSelected = isSelected
            cbIsSelected.visibility = if (isSelected) View.VISIBLE else View.GONE
            tvName.text = priceInformation.first.currencyCode
            tvCategory.text = priceInformation.second
            clParent.setOnClickListener(this)
        }

        //region View.OnClickListener
        override fun onClick(p0: View?) {
            currencyType?.let {
                listener.invoke(arrayOf(it))
            }
        }

        //endregion

        //region ItemDetailsLookup.ItemDetails
        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = absoluteAdapterPosition
                override fun getSelectionKey(): Long = itemId
            }
        //endregion
    }
    //endregion
}
//endregion

//region CurrencyKeyProvider
class CurrencyKeyProvider(private val recyclerView: RecyclerView) :
    ItemKeyProvider<Long>(SCOPE_MAPPED) {

    override fun getKey(position: Int): Long? {
        return recyclerView.adapter?.getItemId(position)
    }

    override fun getPosition(key: Long): Int {
        val viewHolder = recyclerView.findViewHolderForItemId(key)
        return viewHolder?.layoutPosition ?: RecyclerView.NO_POSITION
    }
}
//endregion

//region CurrencyDetailsLookup
class CurrencyDetailsLookup(private val recyclerView: RecyclerView) :
    ItemDetailsLookup<Long>() {
    override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(event.x, event.y)
        if (view != null) {
            return (recyclerView.getChildViewHolder(view) as CurrencyListAdapter.CurrencyViewHolder)
                .getItemDetails()
        }
        return null
    }
}
//endregion