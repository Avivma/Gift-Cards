package com.example.composefirsttry.giftcard.ui.addcard.states

import com.example.composefirsttry.giftcard.logic.cards.model.CardFieldType
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCard
import com.example.composefirsttry.giftcard.ui.common.dialog.common.CustomDialogAdapterItem

sealed class AddCardIntention {
    object OpenCardsDialog : AddCardIntention()
    data class PickCardType(val cardDialogItem: CustomDialogAdapterItem) : AddCardIntention()
    data class FocusCardField(val fieldType: CardFieldType, val hasFocus: Boolean) : AddCardIntention()
    data class SaveCard(val forceSave: Boolean = false) : AddCardIntention()
    object Refresh : AddCardIntention()

    sealed class NavigatedType : AddCardIntention() {
        object AddCard : NavigatedType()
        data class EditCardFromCards(val card: GiftCard) : NavigatedType()
        data class EditCardFromCardDetails(val card: GiftCard) : NavigatedType()
    }
}
