package com.example.composefirsttry.giftcard.ui.addcard.states

import com.example.composefirsttry.giftcard.logic.cards.model.CardFieldType
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCardType

sealed class AddCardIntention {
    object OpenCardsDialog : AddCardIntention()
    data class PickCardType(val cardType: GiftCardType) : AddCardIntention()
    data class FocusCardField(val fieldType: CardFieldType, val hasFocus: Boolean) : AddCardIntention()
    data class SaveCard(val forceSave: Boolean = false) : AddCardIntention()
    object Refresh : AddCardIntention()

    sealed class Navigation : AddCardIntention() {
        object NavigateBackToCardsScreen : AddCardIntention.Navigation()
    }
}
