package com.example.composefirsttry.giftcard.logic.metadata.network.sheet

import java.io.Serializable

data class SheetItem(
    val id: String,
    val index: String,
    val type: String,
    val imageUrl: String,
    val loadMoneyAddress: String,
    val loadMoneyThroughAppOrWeb: String,
): Serializable