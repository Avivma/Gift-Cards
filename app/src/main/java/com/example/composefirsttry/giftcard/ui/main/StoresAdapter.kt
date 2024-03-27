package com.example.composefirsttry.giftcard.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.composefirsttry.R
import com.example.composefirsttry.databinding.StoreRowLayoutBinding
import com.example.composefirsttry.giftcard.logic.stores.model.Store
import com.example.composefirsttry.giftcard.ui.common.adapter.StoreCardsAdapter
import com.example.composefirsttry.giftcard.ui.common.model.ClubIdAndUrl
import com.example.composefirsttry.giftcard.ui.main.states.StoresMainIntention

class StoresAdapter(availableClubsPerStore: MutableMap<Store, List<ClubIdAndUrl>>): RecyclerView.Adapter<StoresAdapter.StoreRowHolder>() {
    private val stores: MutableList<Store> = ArrayList(availableClubsPerStore.keys)
        private val availableClubsPerStore: MutableMap<Store, List<ClubIdAndUrl>> = HashMap(availableClubsPerStore)

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


    override fun getItemId(position: Int): Long {
        return stores[position].storeName.hashCode().toLong()
    }

    fun setStores(storesAndClubs: Map<Store, List<ClubIdAndUrl>>) {
        this.stores.clear()
        this.stores.addAll(storesAndClubs.keys)
        this.availableClubsPerStore.clear()
        this.availableClubsPerStore.putAll(storesAndClubs)
        this.notifyDataSetChanged()
    }

    fun clear() {
        setStores(mutableMapOf())
    }

    fun storeSelected(store: Store) {
        this.notifyItemChanged(stores.indexOf(store))
    }

    //View Holder
    inner class StoreRowHolder(private val binding: StoreRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        private fun createAdapter(store: Store, cardOnClick: (String) -> Unit): StoreCardsAdapter {
            val availableClubs: List<ClubIdAndUrl> = availableClubsPerStore[store]!!
            val adapter = StoreCardsAdapter(availableClubs, cardOnClick)
            adapter.setHasStableIds(true)
            return adapter
        }

        fun binding(store: Store) {
            //set binding variables
            binding.storeName = store.storeName
            binding.storeSelected = store.selected

            //set listeners
            binding.storeNameTv.setOnClickListener {
                mutableIntentionsListener.postValue(StoresMainIntention.SelectStore(store))
            }
            binding.storeNameTv.setOnLongClickListener {
                mutableIntentionsListener.postValue(StoresMainIntention.OpenStoreDialog(store))
                true
            }
            val cardOnClick = { id: String ->
                mutableIntentionsListener.postValue(StoresMainIntention.NavigateToCardsScreen(id))
            }

            binding.recyclerView.adapter = createAdapter(store, cardOnClick)
            binding.recyclerView.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
        }
    }
}