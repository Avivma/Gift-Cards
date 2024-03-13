package com.example.composefirsttry.giftcard.logic.cardsmetadata.db

import android.content.SharedPreferences
import com.example.composefirsttry.giftcard.logic.cardsmetadata.network.sheet.SheetData
import com.example.composefirsttry.giftcard.logic.cardsmetadata.network.sheet.SheetItem
import com.example.composefirsttry.utils.SPKeys
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MetadataCardsDbHandler @Inject constructor(
    private val sp: SharedPreferences,
    private val gson: Gson,
) {
    private lateinit var data: SheetData

    private fun List<SheetItem>.toMapByType(): Map<String, SheetItem> = associateBy { it.type }

    fun saveData(items: List<SheetItem>) {
        save(items)
        build(items)
    }

    fun buildFromSp() {
        if (isMetadataExist()) {
            val items: List<SheetItem> = extractItemsFromSp()
            build(items)
        }
    }

    fun clear() {
        sp.edit().remove(SPKeys.METADATA_CARDS_DATA).commit()
        amount = NONE
    }

    private fun build(items: List<SheetItem>) {
        data = SheetData(items, items.toMapByType())
        amount = data.items.size
    }

    private fun save(items: List<SheetItem>) {
        val dataJson = gson.toJson(items)
        sp.edit().putString(SPKeys.METADATA_CARDS_DATA, dataJson).commit()
    }

    private fun extractItemsFromSp(): List<SheetItem> {
        val dataListJson = sp.getString(SPKeys.METADATA_CARDS_DATA, null)!!
        val type = object : TypeToken<List<SheetItem>>() {}.type
        return gson.fromJson(dataListJson, type)
    }

    fun isSameData(serverItems: List<SheetItem>): Boolean = serverItems == data.items

    fun isMetadataExist(): Boolean = sp.contains(SPKeys.METADATA_CARDS_DATA)

    fun get(): SheetData = data

    companion object {
        private const val NONE = -1
        var amount: Int = NONE
    }
}