package com.example.composefirsttry.giftcard.ui.carddetails.state

import com.example.composefirsttry.giftcard.logic.cards.model.GiftCard

sealed class CardDetailsIntention {
    data class Refresh(val card: GiftCard) : CardDetailsIntention()
    object RemoveCard : CardDetailsIntention()
    object NavigateToEditCard : CardDetailsIntention()
    object OpenRemoveCardDialog : CardDetailsIntention()
    object NavigateOutsideToLoadMoney : CardDetailsIntention()
}
