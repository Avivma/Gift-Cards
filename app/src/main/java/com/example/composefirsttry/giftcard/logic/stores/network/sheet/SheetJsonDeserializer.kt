package com.example.composefirsttry.giftcard.logic.stores.network.sheet

import com.google.gson.*
import java.lang.reflect.Type


class SheetJsonDeserializer(private val amountOfCards: Int) : JsonDeserializer<SheetItem?> {
    //Example:
//    [
//      "ACE",
//      "",
//      "V",
//      "V"
//      .
//      .
//      .
//    ]
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): SheetItem {
        val jsonArray = json.asJsonArray
        val storeName: String = getOrNull(jsonArray, 0)!!
        val cardsAvailability: List<String> = (1 .. amountOfCards).map { i ->
            getOrNull(jsonArray, i) ?: ""
        }
        return SheetItem(storeName = storeName, cardsAvailability = cardsAvailability)
    }

    private fun getOrNull(jsonArray: JsonArray, index: Int): String? {
        return try {
            jsonArray[index].asString
        } catch (e: Exception) {
            null
        }
    }
}