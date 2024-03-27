package com.example.composefirsttry.giftcard.ui.cards.states

import com.example.composefirsttry.giftcard.logic.cards.model.GiftCard

sealed class CardsState {
    data class DisplayData(val giftCards: List<GiftCard>, val showClearAll: Boolean) : CardsState()
    data class RemoveCardDialogOpened(val giftCard: GiftCard) : CardsState()

    sealed class Navigation : CardsState() {
        data class NavigateToEditCard(val card: GiftCard) : CardsState.Navigation()
        object NavigateToAddCard : CardsState.Navigation()
        object NavigateToLandingScreen : CardsState.Navigation()
        data class NavigateToCardDetails(val card: GiftCard) : Navigation()
        data class NavigateOutsideToApplication(val applicationId: String) : CardsState.Navigation()
        data class NavigateOutsideToWebsite(val siteAddress: String) : CardsState.Navigation()
    }
}
