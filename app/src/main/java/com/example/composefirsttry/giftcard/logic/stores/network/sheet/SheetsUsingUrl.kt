package com.example.composefirsttry.giftcard.logic.stores.network.sheet

import androidx.annotation.WorkerThread
import com.example.composefirsttry.giftcard.logic.common.googlesheet.SheetsServiceUrl
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

object SheetsUsingUrl {
    private val itemType = object : TypeToken<List<SheetItem?>?>() {}.type

    @WorkerThread
    fun dataFromWeb(amountOfCards: Int, amountOfStores: Int): List<SheetItem> {
        /*//response data:
        [
          [
            "ACE",
            "",
            "V",
            "V"
            .
            .
            .
          ],
          .
          .
          .
        ]*/
        val column = SheetsServiceUrl.calcColumn('B', amountOfCards)
        val row = SheetsServiceUrl.calcRow(13, amountOfStores)
        val rangeValues = "A13:${column}${row}"
        val jsonArray = SheetsServiceUrl.getDataFromWeb(SheetsServiceUrl.createUrl(rangeValues))
        val gsonBuilder = GsonBuilder()
        val deserializer = SheetJsonDeserializer(amountOfCards)
        gsonBuilder.registerTypeAdapter(SheetItem::class.java, deserializer)
        val gson = gsonBuilder.create()
        val itemList = gson.fromJson<List<SheetItem>>(jsonArray, itemType)
//            L.i("Print itemList")
//            itemList.forEach(Consumer { sheetItem: SheetItem -> L.i(sheetItem.toString()) })
        return itemList
    }
}