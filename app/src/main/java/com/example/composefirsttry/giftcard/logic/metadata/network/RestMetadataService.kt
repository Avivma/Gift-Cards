package com.example.composefirsttry.giftcard.logic.metadata.network

import com.example.composefirsttry.giftcard.logic.metadata.network.sheet.SheetData
import com.example.composefirsttry.giftcard.logic.metadata.network.sheet.SheetItem
import com.example.composefirsttry.giftcard.logic.metadata.network.sheet.SheetsUsingUrl
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestMetadataService @Inject constructor(
    private var sheetsUsingUrl: SheetsUsingUrl
){
    fun getCardsMetadata(): SheetData {
        val (amountOfStores, amountOfCards) = sheetsUsingUrl.getAmounts()
        val sheetItems: List<SheetItem> = sheetsUsingUrl.getGoogleSheetData(amountOfCards)
        return SheetData(amountOfStores, sheetItems)
    }
}