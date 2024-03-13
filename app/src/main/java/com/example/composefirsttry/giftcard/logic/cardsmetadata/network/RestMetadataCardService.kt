package com.example.composefirsttry.giftcard.logic.cardsmetadata.network

import com.example.composefirsttry.giftcard.logic.cardsmetadata.network.sheet.SheetItem
import com.example.composefirsttry.giftcard.logic.cardsmetadata.network.sheet.SheetsUsingUrl
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestMetadataCardService @Inject constructor(
    private var sheetsUsingUrl: SheetsUsingUrl
){
    fun getCardsMetadata(): List<SheetItem> {
        val amountOfCards: Int = sheetsUsingUrl.getAmountOfCards()
        val sheetItems: List<SheetItem> = sheetsUsingUrl.getGoogleSheetData(amountOfCards)
        return sheetItems
    }
}