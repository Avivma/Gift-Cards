package com.example.composefirsttry.giftcard.utils

import com.example.composefirsttry.R
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCardType

object CardUtils {
    @JvmStatic
    fun getCardName(type: GiftCardType): String = when (type) {
        GiftCardType.MAX -> "MAX"
        GiftCardType.ISRACARD -> "ISRACARD"
        GiftCardType.TAV_HAHAM -> "TAV_HAHAM"
    }

    @JvmStatic
    fun getCardType(value: Int): GiftCardType = when (value) {
        GiftCardType.MAX_CARD_VALUE -> GiftCardType.MAX
        GiftCardType.ISRACARD_CARD_VALUE -> GiftCardType.ISRACARD
        GiftCardType.TAV_HAHAM_CARD_VALUE -> GiftCardType.TAV_HAHAM
        else -> throw Exception("Unfamiliar Card Type (type's value = $value)")
    }

    @JvmStatic
    fun getCardImage(type: GiftCardType): Int = when (type) {
        GiftCardType.MAX -> R.drawable.max
        GiftCardType.ISRACARD -> R.drawable.corporate
        GiftCardType.TAV_HAHAM -> R.drawable.hot
    }
}