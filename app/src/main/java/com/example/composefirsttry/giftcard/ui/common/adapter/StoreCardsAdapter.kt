package com.example.composefirsttry.giftcard.ui.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.composefirsttry.R
import com.example.composefirsttry.databinding.GiftCardSmallLayoutBinding
import com.example.composefirsttry.giftcard.ui.common.model.ClubIdAndUrl

class StoreCardsAdapter(private val shoppingClubs: List<ClubIdAndUrl>, private val cardOnClick: ((String) -> Unit)) : RecyclerView.Adapter<StoreCardsAdapter.CardsRowHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreCardsAdapter.CardsRowHolder {
        val binding: GiftCardSmallLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.gift_card_small_layout, parent, false)
        return CardsRowHolder(binding)
    }

    override fun onBindViewHolder(holder: StoreCardsAdapter.CardsRowHolder, position: Int) {
        val club = shoppingClubs[position]
        holder.binding(club)
    }

    override fun getItemCount(): Int {
        return shoppingClubs.size
    }

    //View Holder
    inner class CardsRowHolder(private val binding: GiftCardSmallLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        fun binding(clubDetails: ClubIdAndUrl) {
            //set binding variables
            binding.cardVisible = true
            binding.imageUrl = clubDetails.url

            //set listeners
            binding.cardLayout.setOnClickListener {
                cardOnClick(clubDetails.id)
            }
        }
    }
}