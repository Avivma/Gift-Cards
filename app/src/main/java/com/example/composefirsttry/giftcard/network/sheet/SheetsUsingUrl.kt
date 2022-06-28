package com.example.composefirsttry.giftcard.network.sheet

import androidx.annotation.WorkerThread
import com.example.composefirsttry.L
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request

object SheetsUsingUrl {
    private const val API_KEY = "AIzaSyAcBl6JGeYybR4b_MlgwmEJWHR2TvNcO8o"
    private const val SHEET_ID = "1dHRbVnxrptTIfj66pqANw-2IRNAFH79VpquZtgQLrng"
    private const val SHEET_NAME = "Stores"
    private const val RANGE_VALUES = "A2:D124"
    private const val RANGE = "$SHEET_NAME!$RANGE_VALUES"

    //More info: https://proandroiddev.com/utilising-google-sheets-as-a-realtime-database-for-an-android-application-c56c1a56da2f
    private const val URL = "https://sheets.googleapis.com/v4/spreadsheets/$SHEET_ID/values/$RANGE?key=$API_KEY"
    private const val ROWS_KEY = "values"

    private val itemType = object : TypeToken<List<SheetItem?>?>() {}.type

    @WorkerThread
    fun dataFromWeb(): List<SheetItem> {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(URL)
            .build()
        val response = client.newCall(request).execute()
        val data = response.body()!!.string()
//            printSheet(data)
        val jsonObject = JsonParser.parseString(data).asJsonObject
        val jsonArray = jsonObject.getAsJsonArray(ROWS_KEY)
        val gsonBuilder = GsonBuilder()
        val deserializer = SheetJsonDeserializer()
        gsonBuilder.registerTypeAdapter(SheetItem::class.java, deserializer)
        val gson = gsonBuilder.create()
        val itemList = gson.fromJson<List<SheetItem>>(jsonArray, itemType)
//            L.i("Print itemList")
//            itemList.forEach(Consumer { sheetItem: SheetItem -> L.i(sheetItem.toString()) })
        return itemList
    }

    private fun printSheet(data: String) {
        L.i("Print sheets data")
        val lines = data.split("\\n").toTypedArray()
        for (line in lines) {
            L.i(line)
        }
    }
}