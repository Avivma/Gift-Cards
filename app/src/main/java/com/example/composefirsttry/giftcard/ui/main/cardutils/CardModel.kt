package com.example.composefirsttry.giftcard.ui.main.cardutils

import com.example.composefirsttry.giftcard.model.GiftCard
import com.example.composefirsttry.giftcard.model.GiftCardType

class CardModel(
    private val maxCards: List<GiftCard> = listOf(),
    private val isracardCards: List<GiftCard> = listOf(),
    private val tavHahamCards: List<GiftCard> = listOf()
) {
    fun hasCard(cardType: GiftCardType) = when (cardType) {
        GiftCardType.MAX -> maxCards.isNotEmpty()
        GiftCardType.ISRACARD -> isracardCards.isNotEmpty()
        GiftCardType.TAV_HAHAM -> tavHahamCards.isNotEmpty()
    }

    fun getName(cardType: GiftCardType): String = when (cardType) {
        GiftCardType.MAX -> getName(maxCards)
        GiftCardType.ISRACARD -> getName(isracardCards)
        GiftCardType.TAV_HAHAM -> getName(tavHahamCards)
    }

    private fun getName(cards: List<GiftCard>): String {
        return when (cards.size) {
            0 -> "None"
            1 -> cards[0].name
            else -> "${cards.size} cards}"
        }
    }

    fun getNames(cardType: GiftCardType): String = when (cardType) {
        GiftCardType.MAX -> getNames(maxCards)
        GiftCardType.ISRACARD -> getNames(isracardCards)
        GiftCardType.TAV_HAHAM -> getNames(tavHahamCards)
    }

    private fun getNames(cards: List<GiftCard>): String {
        val message = StringBuilder()
        cards.forEach {
            if (message.isEmpty()) message.append(it.name)
            else message.append(", ${it.name}")
        }
        return message.toString()
    }
}