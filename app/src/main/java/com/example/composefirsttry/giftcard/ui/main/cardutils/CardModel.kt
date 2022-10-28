package com.example.composefirsttry.giftcard.ui.main.cardutils

import com.example.composefirsttry.giftcard.logic.cards.model.GiftCard
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCardType

class CardModel(
    private val maxCards: MutableList<GiftCard> = mutableListOf(),
    private val isracardCards: MutableList<GiftCard> = mutableListOf(),
    private val tavHahamCards: MutableList<GiftCard> = mutableListOf()
) {
    fun addCard(giftCard: GiftCard) = when (giftCard.type) {
        GiftCardType.MAX -> maxCards.add(giftCard)
        GiftCardType.ISRACARD -> isracardCards.add(giftCard)
        GiftCardType.TAV_HAHAM -> tavHahamCards.add(giftCard)
    }

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

    fun getCards(cardType: GiftCardType): List<GiftCard> = when (cardType) {
        GiftCardType.MAX -> maxCards
        GiftCardType.ISRACARD -> isracardCards
        GiftCardType.TAV_HAHAM -> tavHahamCards
    }

    fun getDiscount(cardType: GiftCardType): Float = when (cardType) {
        GiftCardType.MAX -> maxCards.maxOf { it.discount }
        GiftCardType.ISRACARD -> isracardCards.maxOf { it.discount }
        GiftCardType.TAV_HAHAM -> tavHahamCards.maxOf { it.discount }
    }

    private fun getName(cards: List<GiftCard>): String {
        return when (cards.size) {
            0 -> "None"
            1 -> cards[0].name
            else -> "${cards.size} cards"
        }
    }

    fun getNames(cardType: GiftCardType): String = when (cardType) {
        GiftCardType.MAX -> getNames(maxCards)
        GiftCardType.ISRACARD -> getNames(isracardCards)
        GiftCardType.TAV_HAHAM -> getNames(tavHahamCards)
    }

    private fun getNames(cards: List<GiftCard>): String {
        return cards.joinToString { it.name }
/*        val message = StringBuilder()
        cards.forEach {
            if (message.isEmpty()) message.append(it.name)
            else message.append(", ${it.name}")
        }
        return message.toString()*/
    }
}