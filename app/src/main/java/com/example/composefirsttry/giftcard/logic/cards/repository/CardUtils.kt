package com.example.composefirsttry.giftcard.logic.cards.repository

import android.content.SharedPreferences
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCardType
import com.example.composefirsttry.utils.SPKeys
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardUtils @Inject constructor(private val sp: SharedPreferences) {
    fun hasAnyCard(): Boolean = (sp.getInt(SPKeys.GIFT_CARD_AMOUNT_MAX_CARDS, 0) +
                sp.getInt(SPKeys.GIFT_CARD_AMOUNT_ISRACARD_CARDS, 0) +
                sp.getInt(SPKeys.GIFT_CARD_AMOUNT_TAV_HAHAM_CARDS, 0)) > 0

    fun hasCard(cardType: GiftCardType): Boolean {
        return when (cardType) {
            GiftCardType.MAX -> sp.getInt(SPKeys.GIFT_CARD_AMOUNT_MAX_CARDS, 0) > 0
            GiftCardType.ISRACARD -> sp.getInt(SPKeys.GIFT_CARD_AMOUNT_ISRACARD_CARDS, 0) > 0
            GiftCardType.TAV_HAHAM -> sp.getInt(SPKeys.GIFT_CARD_AMOUNT_TAV_HAHAM_CARDS, 0) > 0
        }
    }
}