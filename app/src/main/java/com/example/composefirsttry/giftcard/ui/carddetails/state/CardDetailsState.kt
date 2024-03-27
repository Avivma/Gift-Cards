package com.example.composefirsttry.giftcard.ui.carddetails.state

import com.example.composefirsttry.giftcard.logic.cards.model.GiftCardExtended

sealed class CardDetailsState {
    object RemoveCardDialogOpened : CardDetailsState()
    data class DisplayData(val cardDetailsExtended: GiftCardExtended) : CardDetailsState()

    sealed class Navigation : CardDetailsState() {
        data class NavigateToEditCard(val card: GiftCardExtended) : Navigation()
        object NavigateToLandingScreen : Navigation()
        object NavigateBackToCards : Navigation()
        data class NavigateOutsideToApplication(val applicationId: String) : Navigation()
        data class NavigateOutsideToWebsite(val siteAddress: String) : Navigation()
    }
}
