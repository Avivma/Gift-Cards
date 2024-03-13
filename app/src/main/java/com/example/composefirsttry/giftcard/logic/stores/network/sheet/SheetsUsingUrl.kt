package com.example.composefirsttry.giftcard.logic.stores.network.sheet

import androidx.annotation.WorkerThread
import com.example.composefirsttry.giftcard.logic.common.googlesheet.SheetsServiceUrl
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

object SheetsUsingUrl {
    private const val RANGE_VALUES = "A8:H500"
    private val itemType = object : TypeToken<List<SheetItem?>?>() {}.type

    @WorkerThread
    fun dataFromWeb(): List<SheetItem> {
        val jsonArray = SheetsServiceUrl.getDataFromWeb(SheetsServiceUrl.createUrl(RANGE_VALUES))
        val gsonBuilder = GsonBuilder()
        val deserializer = SheetJsonDeserializer()
        gsonBuilder.registerTypeAdapter(SheetItem::class.java, deserializer)
        val gson = gsonBuilder.create()
        val itemList = gson.fromJson<List<SheetItem>>(jsonArray, itemType)
//            L.i("Print itemList")
//            itemList.forEach(Consumer { sheetItem: SheetItem -> L.i(sheetItem.toString()) })
        return itemList
    }
}