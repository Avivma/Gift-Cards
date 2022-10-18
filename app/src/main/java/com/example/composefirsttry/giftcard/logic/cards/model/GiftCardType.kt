package com.example.composefirsttry.giftcard.logic.cards.model

import androidx.annotation.DrawableRes
import com.example.composefirsttry.R

enum class GiftCardType (val value: Int, @DrawableRes val imageRes:  Int) {
    MAX(GiftCardType.MAX_CARD_VALUE, R.drawable.max),
    ISRACARD(GiftCardType.ISRACARD_CARD_VALUE, R.drawable.corporate),
    TAV_HAHAM(GiftCardType.TAV_HAHAM_CARD_VALUE, R.drawable.hot);

    fun getCardType(value: Int): GiftCardType {
        return when(value) {
            MAX_CARD_VALUE -> MAX
            ISRACARD_CARD_VALUE -> ISRACARD
            TAV_HAHAM_CARD_VALUE -> TAV_HAHAM
            else -> throw Exception("Unfamiliar Card Type (type's value = $value)")
        }
    }

    companion object {
        const val MAX_CARD_VALUE = 1
        const val ISRACARD_CARD_VALUE = 2
        const val TAV_HAHAM_CARD_VALUE = 3
    }
}