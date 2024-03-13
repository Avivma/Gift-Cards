package com.example.composefirsttry.giftcard.logic.cardsmetadata.network.sheet

import java.io.Serializable

data class SheetItem(
    val id: String,
    val type: String = "",
    val imageUrl: String = "",
): Serializable