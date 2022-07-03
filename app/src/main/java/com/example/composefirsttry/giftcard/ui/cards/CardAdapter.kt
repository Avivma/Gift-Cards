package com.example.composefirsttry.giftcard.ui.cards

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.composefirsttry.R
import com.example.composefirsttry.databinding.CardDetailsLayoutBinding
import com.example.composefirsttry.giftcard.model.GiftCard

class CardAdapter (giftCards: List<GiftCard>): RecyclerView.Adapter<CardAdapter.CardHolder>() {
    private val cards: MutableList<GiftCard> = ArrayList(giftCards)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        val binding: CardDetailsLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.card_details_layout, parent, false)
        return CardHolder(binding)
    }

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        val card = cards[position]
        holder.binding(card)
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    fun setCards(cards: List<GiftCard>) {
        this.cards.clear()
        this.cards.addAll(cards)
        notifyDataSetChanged()
    }

    //View Holder
    inner class CardHolder(private val binding: CardDetailsLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        fun binding(card: GiftCard) {
            binding.card = card
        }
    }
}