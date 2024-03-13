package com.example.composefirsttry.giftcard.logic.common.googlesheet

import androidx.annotation.WorkerThread
import com.example.composefirsttry.L
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import okhttp3.OkHttpClient
import okhttp3.Request


object SheetsServiceUrl {
    private const val API_KEY = "AIzaSyAcBl6JGeYybR4b_MlgwmEJWHR2TvNcO8o"
    private const val SHEET_ID = "1dHRbVnxrptTIfj66pqANw-2IRNAFH79VpquZtgQLrng"
    private const val SHEET_NAME = "Stores"
    private const val ROWS_KEY = "values"
    fun createUrl(rangeValues: String): String { //URL
        //More info: https://proandroiddev.com/utilising-google-sheets-as-a-realtime-database-for-an-android-application-c56c1a56da2f
        return "https://sheets.googleapis.com/v4/spreadsheets/$SHEET_ID/values/${getRange(rangeValues)}?key=$API_KEY"
    }
    private fun getRange(rangeValues: String): String { //RANGE
        return "$SHEET_NAME!$rangeValues"
    }

    @WorkerThread
    fun getDataFromWeb(url: String): JsonArray {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()
        val response = client.newCall(request).execute()
        val data = response.body()!!.string()
//            printSheet(data)
        val jsonObject = JsonParser.parseString(data).asJsonObject
        return  jsonObject.getAsJsonArray(ROWS_KEY)
    }

    private fun printSheet(data: String) {
        L.i("Print sheets data")
        val lines = data.split("\\n").toTypedArray()
        for (line in lines) {
            L.i(line)
        }
    }
}