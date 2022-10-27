package com.example.composefirsttry.giftcard.logic.cards.model

sealed class CardSecureFields(
    val number: String,
    val cvv: String,
    val expirationDate: String
) {
    class Keys(
        number: String,
        cvv: String,
        expirationDate: String
    ) : CardSecureFields(number,cvv, expirationDate)
    class Values(
        number: String,
        cvv: String,
        expirationDate: String
    ) : CardSecureFields(number,cvv, expirationDate)
}
