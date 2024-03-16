package com.example.composefirsttry.giftcard.logic.cardsmetadata.network.sheet

import androidx.annotation.WorkerThread
import com.example.composefirsttry.giftcard.logic.common.googlesheet.SheetsServiceUrl
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SheetsUsingUrl @Inject constructor() {
    @WorkerThread
    fun getAmountOfCards(): Int {
        /*//response data:
        {
            "range": "Stores!C2",
            "majorDimension": "ROWS",
            "values": [
              [
                  "4"
              ]
            ]
        }
        //note: getDataFromWeb() return the JsonArray after "values"
        */
        val rangeValues = "C2:C2"
        val jsonArray = SheetsServiceUrl.getDataFromWeb(SheetsServiceUrl.createUrl(rangeValues))
        val cardsAmount: Int = getIntFromJsonArray(jsonArray)
        return cardsAmount
    }

    private fun getIntFromJsonArray(jsonArray: JsonArray): Int {
//    [["4"]]
        return jsonArray[0].asJsonArray[0].asInt
    }

    @WorkerThread
    fun getGoogleSheetData(amountOfCards: Int): List<SheetItem> {
        /*//response data:
        {
            "range": "Stores!B3:E5",
            "majorDimension": "ROWS",
            "values": [
            [
                "1",
                "2",
                "3",
                "4"
            ],
            [
                "tav_haham",
                "isracard",
                "max",
                "beyahad"
            ],
            [
                "https://drive.google.com/file/d/1FM54OOt_vDzWKtlqC9u0q9ADHZ9Cpina/view?usp=drive_link",
                "https://drive.google.com/file/d/1eH5g-ARj-8mboy0JBDyOVMhyIUfulz7z/view?usp=drive_link",
                "https://drive.google.com/file/d/1uXgvNSanXKyClbFd7ooG4vEevEw4vU20/view?usp=drive_link",
                "https://drive.google.com/file/d/14Gfi4UGZiI-w79B-qlQi6lEt0cP9SBvm/view?usp=drive_link"
            ]
            ]
        }
        //note: getDataFromWeb() return the JsonArray after "values"
        */
        val column = calcColumn(amountOfCards)
        val rangeValues = "B3:${column}5"
        val jsonArray = SheetsServiceUrl.getDataFromWeb(SheetsServiceUrl.createUrl(rangeValues))
        val items: List<SheetItem> = parseJsonArray(jsonArray, amountOfCards)
        return items
    }

    private fun parseJsonArray(jsonArray: JsonArray, itemsAmount: Int): List<SheetItem> {
        val gson = Gson()
        val type = object : TypeToken<List<List<String>>>() {}.type
        val lists: List<List<String>> = gson.fromJson(jsonArray, type)
        val (ids, types, imageLinks) = lists

        return List(itemsAmount) { i ->
            SheetItem(ids[i], types[i], manipulateDriveUrl(imageLinks[i]))
        }
    }

    private fun manipulateDriveUrl(driveUrl: String): String {
        // convert driveUrl: "https://drive.google.com/file/d/14Gfi4UGZiI-w79B-qlQi6lEt0cP9SBvm/view"
        // to this:          "https://drive.usercontent.google.com/download?id=14Gfi4UGZiI-w79B-qlQi6lEt0cP9SBvm&export=view"
        val baseUrl = "https://drive.google.com/file/d/"
        val directDownloadBaseUrl = "https://drive.usercontent.google.com/download?id="
        if (!driveUrl.startsWith(baseUrl)) {
            return "Invalid URL" // Early return if the URL doesn't start with the expected base URL
        }
        val fileIdPart = driveUrl.substringAfter(baseUrl, "")
        val fileId = fileIdPart.substringBefore("/view", "")
        return if (fileId.isNotEmpty()) {
            directDownloadBaseUrl + fileId
        } else {
            "Invalid URL" // Return an error message or handle as needed
        }
    }

    private fun calcColumn(amountOfCards: Int): Char {
        val offsetAddition: Int = amountOfCards - 1
        val startColumnLetter: Char = 'B'
        val endColumn = calcAsciiCapital(startColumnLetter, offsetAddition)
        return endColumn
    }

    private fun calcAsciiCapital(letter: Char, offset: Int): Char {
        val asciiValueOfA = letter.code // ASCII value of 'B' is 66
        return (asciiValueOfA + offset).toChar()
    }
}