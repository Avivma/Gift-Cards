package com.example.composefirsttry.giftcard.logic.cards.model

import androidx.annotation.DrawableRes
import com.example.composefirsttry.R
import java.io.Serializable

sealed class GiftCard(
    val name: String,
    val discount: Float,
    @DrawableRes val imageRes: Int,
    var longName: String,
    var number: String,
    var cvv: String,
    var expirationDate: String,
    var operator: String
): Serializable {
    object MAX : GiftCard(
        MAX_CARD_NAME,
        16.5f,
        imageRes = R.drawable.max,
        "Gift Card MAX executive",
        "1111-1111-1111-111",
        "999",
        "01/26",
        "MASTERCARD"
    )

    object CORPORATE : GiftCard(
        CORPORATE_CARD_NAME,
        19f,
        imageRes = R.drawable.corporate,
        "GiftCard ישראכרט",
        "2222-2222-2222-222",
        "888",
        "10/25",
        "ישראכרט"
    )

    object HOT : GiftCard(
        HOT_CARD_NAME,
        15f,
        imageRes = R.drawable.hot,
        "הוט התו החכם",
        "3333-3333-3333-333",
        "777",
        "12/24",
        "MASTERCARD"
    )

    companion object {
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