package com.example.composefirsttry.giftcard.logic.cards.model

import java.io.Serializable

open class GiftCard(
    open var id: Int = -1,
    open val cardClubId: String,
    open val name: String,
    open val imageUrl: String,
    open var discount: Float
): Serializable

class GiftCardExtended(
    override var id: Int = -1,
    override val cardClubId: String,
    override val name: String,
    override val imageUrl: String,
    override var discount: Float,
    var longName: String = "",
    var number: String = "",
    var cvv: String = "",
    var expirationDate: String = "",
    var operator: String = "") : GiftCard(id, cardClubId, name, imageUrl, discount)

// card id
// card image
// card name
// card discount
// card longName
// card number
// card date
// card cvv
// card operator (VIZA, MASTERCARD)
// card isOutsideBank