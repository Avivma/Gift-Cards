package com.example.composefirsttry.giftcard.logic.metadata.network.sheet

import java.io.Serializable

data class SheetData(
    val storeAmount: Int,
    val cardItems: List<SheetItem>
): Serializable