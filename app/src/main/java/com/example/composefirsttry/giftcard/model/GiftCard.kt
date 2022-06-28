package com.example.composefirsttry.giftcard.model

sealed class GiftCard(val name: String, val discount: Float, val imageName: String) {
    object MAX: GiftCard(MAX_CARD_NAME, 16.5f, "max.bmp")
    object CORPORATE: GiftCard(CORPORATE_CARD_NAME, 19f, "corporate.bmp")
    object HOT: GiftCard(HOT_CARD_NAME, 15f, "hot.bmp")

    companion object {
        const val MAX_CARD_NAME = "Max"
        const val CORPORATE_CARD_NAME = "Corporate"
        const val HOT_CARD_NAME = "Hot"
    }
}