package com.example.composefirsttry.giftcard.logic.cards.model

import java.io.Serializable

sealed class GiftCardType(val value: Int) : Serializable {
    object MAX : GiftCardType(MAX_CARD_VALUE)
    object ISRACARD : GiftCardType(ISRACARD_CARD_VALUE)
    object TAV_HAHAM : GiftCardType(TAV_HAHAM_CARD_VALUE)

    companion object {
        const val MAX_CARD_VALUE = 1
        const val ISRACARD_CARD_VALUE = 2
        const val TAV_HAHAM_CARD_VALUE = 3
    }
}