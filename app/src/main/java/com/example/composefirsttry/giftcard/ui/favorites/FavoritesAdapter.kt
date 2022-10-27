package com.example.composefirsttry.giftcard.ui.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.composefirsttry.R
import com.example.composefirsttry.databinding.StoreRowLayoutBinding
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCardType
import com.example.composefirsttry.giftcard.logic.cards.repository.CardUtils
import com.example.composefirsttry.giftcard.logic.stores.model.Store
import com.example.composefirsttry.giftcard.ui.favorites.states.FavoritesIntention

class FavoritesAdapter (stores: List<Store>, private val cardUtils: CardUtils): RecyclerView.Adapter<FavoritesAdapter.StoreRowHolder>() {
    private val stores: MutableList<Store> = ArrayList(stores)

    var intentionsListener: LiveData<FavoritesIntention> = MutableLiveData()
    private var mutableIntentionsListener: MutableLiveData<FavoritesIntention> =
        intentionsListener as MutableLiveData<FavoritesIntention>

    private var maxCardExist: Boolean = false
    private var corporateCardExist: Boolean = false
    private var hotCardExist: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreRowHolder {
        val binding: StoreRowLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.store_row_layout,
            parent,
            false
        )
        return StoreRowHolder(binding)
    }

    override fun onBindViewHolder(holder: StoreRowHolder, position: Int) {
        val store = stores[position]
        holder.binding(store)
    }

    override fun getItemCount(): Int {
        return stores.size
    }

    override fun getItemId(position: Int): Long {
        return stores[position].storeName.hashCode().toLong()
    }

    fun setStores(giftCards: List<Store>) {
        maxCardExist = cardUtils.hasCard(GiftCardType.MAX)
        corporateCardExist = cardUtils.hasCard(GiftCardType.ISRACARD)
        hotCardExist = cardUtils.hasCard(GiftCardType.TAV_HAHAM)
        this.stores.clear()
        this.stores.addAll(giftCards)
        this.notifyDataSetChanged()
    }

    fun clear() {
        setStores(mutableListOf())
    }

    //View Holder
    inner class StoreRowHolder(private val binding: StoreRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun binding(store: Store) {
            //set binding variables
            binding.maxCardVisible = store.maxCard && maxCardExist
            binding.corporateCardVisible = store.corporateCard && corporateCardExist
            binding.hotCardVisible = store.hotCard && hotCardExist
            binding.storeName = store.storeName
            binding.storeSelected = false

            //set listeners
            binding.cardMax.cardLayout.setOnClickListener { mutableIntentionsListener.postValue(FavoritesIntention.NavigateToCardsScreen(GiftCardType.MAX)) }
            binding.cardCorporate.cardLayout.setOnClickListener { mutableIntentionsListener.postValue(FavoritesIntention.NavigateToCardsScreen(GiftCardType.ISRACARD)) }
            binding.cardHot.cardLayout.setOnClickListener { mutableIntentionsListener.postValue(FavoritesIntention.NavigateToCardsScreen(GiftCardType.TAV_HAHAM)) }
            binding.storeNameTv.setOnLongClickListener {
                mutableIntentionsListener.postValue(FavoritesIntention.OpenStoreDialog(store))
                true
            }
        }
    }
}