package com.example.composefirsttry.giftcard.model

data class Store constructor(
    var storeName: String,
    var maxCard: Boolean = false,
    var corporateCard: Boolean = false,
    var hotCard: Boolean = false
) {
}