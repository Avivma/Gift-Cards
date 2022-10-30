package com.example.composefirsttry.giftcard.ui.cards.states

import com.example.composefirsttry.giftcard.logic.cards.model.GiftCard
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCardType

sealed class CardsIntention {
    data class OpenRemoveCardDialog(val card: GiftCard) : CardsIntention()
    data class RemoveCard(val card: GiftCard) : CardsIntention()
    object ClearAll : CardsIntention()
    object Refresh : CardsIntention()
    data class NavigateToEditCard(val card: GiftCard) : CardsIntention()

    sealed class NavigatedType : CardsState() {
        object ShowAll : NavigatedType()
        data class SingleCard(val argCardType: GiftCardType) : NavigatedType()
    }
}
