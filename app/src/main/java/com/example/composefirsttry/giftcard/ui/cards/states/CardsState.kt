package com.example.composefirsttry.giftcard.ui.cards.states

import com.example.composefirsttry.giftcard.logic.cards.model.GiftCard

sealed class CardsState {
    data class DisplayData(val giftCards: List<GiftCard>, val showClearAll: Boolean) : CardsState()
    data class RemoveCardDialogOpened(val giftCard: GiftCard) : CardsState()
}
