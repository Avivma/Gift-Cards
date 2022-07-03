package com.example.composefirsttry.giftcard.ui.main

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.composefirsttry.R
import com.example.composefirsttry.databinding.StoreRowLayoutBinding
import com.example.composefirsttry.giftcard.model.Store
import com.example.composefirsttry.utils.SPKeys

class StoresAdapter(giftCards: List<Store>, var sp: SharedPreferences): RecyclerView.Adapter<StoresAdapter.StoreRowHolder>() {
    private val stores: MutableList<Store> = ArrayList(giftCards)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreRowHolder {
        val binding: StoreRowLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.store_row_layout, parent, false)
        return StoreRowHolder(binding)
    }

    override fun onBindViewHolder(holder: StoreRowHolder, position: Int) {
        val store = stores[position]
        holder.binding(store)
    }

    override fun getItemCount(): Int {
        return stores.size
    }

    private var maxCardChecked: Boolean = true
    private var corporateCardChecked: Boolean = true
    private var hotCardChecked: Boolean = true

    fun setStores(giftCards: List<Store>) {
        maxCardChecked = sp.getBoolean(SPKeys.GIFT_CARD_MAX_CHECKBOX_STATE, true)
        corporateCardChecked = sp.getBoolean(SPKeys.GIFT_CARD_CORPORATE_CHECKBOX_STATE, true)
        hotCardChecked = sp.getBoolean(SPKeys.GIFT_CARD_HOT_CHECKBOX_STATE, true)
        this.stores.clear()
        this.stores.addAll(giftCards)
        notifyDataSetChanged()
    }



    fun clear() {
        setStores(mutableListOf())
    }

    //View Holder
    inner class StoreRowHolder(private val binding: StoreRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        fun binding(store: Store) {
            binding.store = store
            binding.maxCardChecked = maxCardChecked
            binding.corporateCardChecked = corporateCardChecked
            binding.hotCardChecked = hotCardChecked
        }
    }
}