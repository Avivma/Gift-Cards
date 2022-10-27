package com.example.composefirsttry.giftcard.logic.cards.model

import com.example.composefirsttry.R
import java.io.Serializable

enum class GiftCardType(val value: Int): Serializable {
    MAX(GiftCardType.MAX_CARD_VALUE),
    ISRACARD(GiftCardType.ISRACARD_CARD_VALUE),
    TAV_HAHAM(GiftCardType.TAV_HAHAM_CARD_VALUE);

    companion object {
        const val MAX_CARD_VALUE = 1
        const val ISRACARD_CARD_VALUE = 2
        const val TAV_HAHAM_CARD_VALUE = 3

        fun getCardType(value: Int): GiftCardType = when (value) {
            MAX_CARD_VALUE -> MAX
            ISRACARD_CARD_VALUE -> ISRACARD
            TAV_HAHAM_CARD_VALUE -> TAV_HAHAM
            else -> throw Exception("Unfamiliar Card Type (type's value = $value)")
        }

        @JvmStatic
        fun getCardImage(type: GiftCardType): Int = when (type) {
            MAX -> R.drawable.max
            ISRACARD -> R.drawable.corporate
            TAV_HAHAM -> R.drawable.hot
        }
    }
}