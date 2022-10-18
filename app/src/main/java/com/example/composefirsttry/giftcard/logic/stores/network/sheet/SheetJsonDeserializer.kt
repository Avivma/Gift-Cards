package com.example.composefirsttry.giftcard.logic.stores.network.sheet

import com.google.gson.*
import java.lang.reflect.Type


class SheetJsonDeserializer : JsonDeserializer<SheetItem?> {
    //Example:
//    [
//      "ACE",
//      "",
//      "V",
//      "V"
//    ]
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): SheetItem {
        val jsonArray = json.asJsonArray

        val storeName: String = getOrNull(jsonArray, 0)!!
        val hotAvailability: String = getOrNull(jsonArray, 1) ?: ""
        val corporateAvailability: String = getOrNull(jsonArray, 2) ?: ""
        val maxAvailability: String =  getOrNull(jsonArray, 3) ?: ""
        return SheetItem(storeName, hotAvailability, corporateAvailability, maxAvailability)
    }

    private fun getOrNull(jsonArray: JsonArray, index: Int): String? {
        return try {
            jsonArray[index].asString
        } catch (e: Exception) {
            null
        }
    }
}