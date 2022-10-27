package com.example.composefirsttry.giftcard.ui.cards.states

import com.example.composefirsttry.giftcard.logic.cards.model.GiftCard

sealed class CardsIntention {
    data class OpenRemoveCardDialog(val card: GiftCard) : CardsIntention()
    data class RemoveCard(val card: GiftCard) : CardsIntention()
    object ClearAll : CardsIntention()
    object Refresh : CardsIntention()

    sealed class Navigation: CardsIntention() {
        data class NavigateToEditCard(val card: GiftCard): CardsIntention.Navigation()
        object NavigateToAddCard: CardsIntention.Navigation()
        object NavigateToLandingScreen: CardsIntention.Navigation()
    }
}
