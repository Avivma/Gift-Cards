package com.example.composefirsttry.giftcard.logic.cards.model

import androidx.annotation.DrawableRes
import java.io.Serializable

open class GiftCard(
    val type: GiftCardType,
    val name: String,
    @DrawableRes val imageRes: Int,
    var discount: Float,
    var longName: String = "",
    var number: String = "",
    var cvv: String = "",
    var expirationDate: String = "",
    var operator: String = ""
): Serializable {
    object MAX : GiftCard(
        GiftCardType.MAX,
        MAX_CARD_NAME,
        imageRes = GiftCardType.getCardImage(GiftCardType.MAX),
        16.5f,
        "Gift Card MAX executive",
        "1111-1111-1111-111",
        "999",
        "01/26",
        "MASTERCARD"
    )

    object CORPORATE : GiftCard(
        GiftCardType.ISRACARD,
        CORPORATE_CARD_NAME,
        imageRes = GiftCardType.getCardImage(GiftCardType.ISRACARD),
        19f,
        "GiftCard ישראכרט",
        "2222-2222-2222-222",
        "888",
        "10/25",
        "ישראכרט"
    )

    object HOT : GiftCard(
        GiftCardType.TAV_HAHAM,
        HOT_CARD_NAME,
        imageRes = GiftCardType.getCardImage(GiftCardType.TAV_HAHAM),
        15f,
        "הוט התו החכם",
        "3333-3333-3333-333",
        "777",
        "12/24",
        "MASTERCARD"
    )

    private companion object {
        const val MAX_CARD_NAME = "Max"
        const val CORPORATE_CARD_NAME = "Corporate"
        const val HOT_CARD_NAME = "Hot"
    }
}


// card image
// card name
// card discount
// card longName
// card number
// card date
// card cvv
// card operator (VIZA, MASTERCARD)
// card isOutsideBank