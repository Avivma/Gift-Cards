package com.example.composefirsttry.giftcard.ui.addcard.states

import com.example.composefirsttry.giftcard.logic.cards.model.CardFieldStatus
import com.example.composefirsttry.giftcard.logic.cards.model.CardFieldType
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCardExtended
import com.example.composefirsttry.giftcard.ui.common.dialog.common.CustomDialogAdapterItem
import com.example.composefirsttry.giftcard.ui.common.model.ClubIdAndUrl
import java.util.*

sealed class AddCardState {
    data class DisplayData(
        val fieldsStatusMap: Map<CardFieldType, CardFieldStatus>,
        val displayPartialErrorDialog: Boolean
    ) : AddCardState()

    data class DisplayDataEditCard(
        val fieldsValueMap: HashMap<CardFieldType, String>,
        val clubDetails: ClubIdAndUrl
    ) : AddCardState()

    data class FieldStatusChanged(val fieldType: CardFieldType, val statusOk: Boolean) : AddCardState()
    data class CardImageChanged(val clubDetails: ClubIdAndUrl) : AddCardState()

    data class CardsDialogOpened(val dialogItems: List<CustomDialogAdapterItem>) : AddCardState()

    sealed class Navigation : AddCardState() {
        data class NavigateBackToCardDetails(val card: GiftCardExtended) : Navigation()
        object NavigateBackToCardsScreen : AddCardState.Navigation()
    }
}
