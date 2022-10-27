package com.example.composefirsttry.giftcard.ui.addcard.states

import com.example.composefirsttry.giftcard.logic.cards.model.CardFieldStatus
import com.example.composefirsttry.giftcard.logic.cards.model.CardFieldType
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCardType
import java.util.*

sealed class AddCardState {
    data class DisplayData(
        val fieldsStatusMap: Map<CardFieldType, CardFieldStatus>,
        val displayPartialErrorDialog: Boolean
    ) : AddCardState()

    data class DisplayDataEditCard(
        val fieldsValueMap: HashMap<CardFieldType, String>,
        val cardType: GiftCardType
    ) : AddCardState()

    data class FieldStatusChanged(val fieldType: CardFieldType, val statusOk: Boolean) : AddCardState()
    data class CardImageChanged(val cardType: GiftCardType) : AddCardState()

    object CardsDialogOpened : AddCardState()
}
