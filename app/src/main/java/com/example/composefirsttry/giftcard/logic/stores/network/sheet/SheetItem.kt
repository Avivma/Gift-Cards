package com.example.composefirsttry.giftcard.logic.stores.network.sheet

data class SheetItem(
    val storeName: String,
    val hotAvailability: String = "",
    val corporateAvailability: String = "",
    val maxAvailability: String = ""
)