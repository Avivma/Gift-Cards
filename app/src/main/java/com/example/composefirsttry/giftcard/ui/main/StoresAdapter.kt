package com.example.composefirsttry.giftcard.ui.main

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.composefirsttry.R
import com.example.composefirsttry.databinding.StoreRowLayoutBinding
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCard
import com.example.composefirsttry.giftcard.logic.stores.model.Store
import com.example.composefirsttry.giftcard.ui.main.states.StoresMainIntention
import com.example.composefirsttry.utils.SPKeys

class StoresAdapter(giftCards: List<Store>, var context: Context, var sp: SharedPreferences): RecyclerView.Adapter<StoresAdapter.StoreRowHolder>() {
    private val stores: MutableList<Store> = ArrayList(giftCards)

    var intentionsListener: LiveData<StoresMainIntention> = MutableLiveData()
    private var mutableIntentionsListener: MutableLiveData<StoresMainIntention> = intentionsListener as MutableLiveData<StoresMainIntention>

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

    override fun getItemId(position: Int): Long {
        return stores[position].storeName.hashCode().toLong()
    }

    fun setStores(giftCards: List<Store>) {
        maxCardChecked = sp.getBoolean(SPKeys.GIFT_CARD_MAX_CHECKBOX_STATE, true)
        corporateCardChecked = sp.getBoolean(SPKeys.GIFT_CARD_CORPORATE_CHECKBOX_STATE, true)
        hotCardChecked = sp.getBoolean(SPKeys.GIFT_CARD_HOT_CHECKBOX_STATE, true)
        this.stores.clear()
        this.stores.addAll(giftCards)
        this.notifyDataSetChanged()
    }

    fun clear() {
        setStores(mutableListOf())
    }

    fun storeSelected(store: Store) {
        this.notifyItemChanged(stores.indexOf(store))
    }

    //View Holder
    inner class StoreRowHolder(private val binding: StoreRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        fun binding(store: Store) {
            //set binding variables
            binding.store = store
            binding.maxCardChecked = maxCardChecked
            binding.corporateCardChecked = corporateCardChecked
            binding.hotCardChecked = hotCardChecked
            binding.storeSelected = store.selected

            //set listeners
            binding.cardMax.cardLayout.setOnClickListener { mutableIntentionsListener.postValue(StoresMainIntention.Navigation.NavigateToCardsScreen(
                GiftCard.MAX)) }
            binding.cardCorporate.cardLayout.setOnClickListener { mutableIntentionsListener.postValue(StoresMainIntention.Navigation.NavigateToCardsScreen(
                GiftCard.CORPORATE)) }
            binding.cardHot.cardLayout.setOnClickListener { mutableIntentionsListener.postValue(StoresMainIntention.Navigation.NavigateToCardsScreen(
                GiftCard.HOT)) }
            binding.storeName.setOnClickListener {
                mutableIntentionsListener.postValue(StoresMainIntention.SelectStore(store))
            }
            binding.storeName.setOnLongClickListener {
                mutableIntentionsListener.postValue(StoresMainIntention.OpenStoreDialog(store))
                true
            }
        }
    }
}