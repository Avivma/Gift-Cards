package com.example.composefirsttry.giftcard.logic.cardsmetadata.network.sheet

data class SheetData(
    val items: List<SheetItem>,
    var map: Map<String, SheetItem>
)