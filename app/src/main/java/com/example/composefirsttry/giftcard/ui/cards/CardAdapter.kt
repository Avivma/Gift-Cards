package com.example.composefirsttry.giftcard.ui.cards

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.composefirsttry.R
import com.example.composefirsttry.databinding.CardRowFooterLayoutBinding
import com.example.composefirsttry.databinding.CardRowLayoutBinding
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCard
import com.example.composefirsttry.giftcard.ui.cards.states.CardsIntention

class CardAdapter(giftCards: List<GiftCard>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val cards: MutableList<GiftCard> = ArrayList(giftCards)

    var intentionsListener: LiveData<CardsIntention> = MutableLiveData()
    private var mutableIntentionsListener: MutableLiveData<CardsIntention> = intentionsListener as MutableLiveData<CardsIntention>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CARD_HOLDER -> {
                val binding: CardRowLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.card_row_layout, parent, false)
                CardHolder(binding)
            }
            FOOTER -> {
                val binding: CardRowFooterLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.card_row_footer_layout, parent, false)
                CardHolderFooter(binding)
            }
            else -> throw Exception("Unfamiliar ViewHolder type ($viewType)")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CardHolder) {
            val card = cards[position]
            holder.binding(card)
        }
    }

    override fun getItemCount(): Int {
        return cards.size + 1
    }

    override fun getItemViewType(position: Int): Int =
        if (position < cards.size) CARD_HOLDER else FOOTER

    override fun getItemId(position: Int): Long {
        return if (position < cards.size)
            (cards[position].type.value.toString() + cards[position].name).hashCode().toLong()
        else -1L
    }

    fun setCards(cards: List<GiftCard>) {
        this.cards.clear()
        this.cards.addAll(cards)
        notifyDataSetChanged()
    }

    //View Holder
    inner class CardHolder(private val binding: CardRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        fun binding(card: GiftCard) {
            binding.card = card

            //set listeners
            binding.edit.setOnClickListener {
                mutableIntentionsListener.postValue(CardsIntention.NavigateToEditCard(card))
            }
            binding.remove.setOnClickListener {
                mutableIntentionsListener.postValue(CardsIntention.OpenRemoveCardDialog(card))
            }
            binding.loadMoney.setOnClickListener {
                mutableIntentionsListener.postValue(CardsIntention.NavigateOutsideToLoadMoney(card))
            }
            binding.image.setOnClickListener {
                mutableIntentionsListener.postValue(CardsIntention.NavigateToCardDetails(card))
            }
        }
    }
    inner class CardHolderFooter(binding: CardRowFooterLayoutBinding): RecyclerView.ViewHolder(binding.root)

    companion object {
        private const val CARD_HOLDER = 1
        private const val FOOTER = 2
    }
}