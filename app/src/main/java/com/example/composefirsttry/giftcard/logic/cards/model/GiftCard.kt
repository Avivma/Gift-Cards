package com.example.composefirsttry.giftcard.logic.cards.model

import androidx.annotation.DrawableRes
import java.io.Serializable

open class GiftCard(
    open var id: Int = -1,
    open val type: GiftCardType,
    open val name: String,
    @DrawableRes open val imageRes: Int,
    open var discount: Float
): Serializable

class GiftCardExtended(
    override var id: Int = -1,
    override val type: GiftCardType,
    override val name: String,
    @DrawableRes override val imageRes: Int,
    override var discount: Float,
    var longName: String = "",
    var number: String = "",
    var cvv: String = "",
    var expirationDate: String = "",
    var operator: String = "") : GiftCard(id, type, name, imageRes, discount)

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