package com.example.composefirsttry.giftcard.logic.cards.model

import androidx.annotation.DrawableRes
import java.io.Serializable

data class GiftCardPlainInfo (
    val type: GiftCardType,
    val name: String,
    @DrawableRes val imageRes: Int,
    val discount: Float): Serializable