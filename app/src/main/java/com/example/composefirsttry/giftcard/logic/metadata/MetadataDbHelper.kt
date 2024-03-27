package com.example.composefirsttry.giftcard.logic.metadata

import android.content.SharedPreferences
import com.example.composefirsttry.giftcard.logic.metadata.network.sheet.SheetData
import com.example.composefirsttry.giftcard.logic.metadata.network.sheet.SheetItem
import com.example.composefirsttry.utils.SPKeys
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MetadataDbHelper @Inject constructor(
    private val sp: SharedPreferences,
) {
//    private fun List<SheetItem>.toMapByType(): Map<String, SheetItem> = associateBy { it.type }

    fun saveMetadata(data: SheetData) {
        save(data)
    }

    fun clear() {
        sp.edit()
            .remove(SPKeys.METADATA_CARDS_HASH)
            .remove(SPKeys.METADATA_CARDS_AMOUNT)
            .commit()
    }

    private fun save(data: SheetData) {
        sp.edit()
            .putInt(SPKeys.METADATA_CARDS_HASH, calcHash(data.cardItems))
            .putInt(SPKeys.METADATA_CARDS_AMOUNT, data.cardItems.size)
            .putInt(SPKeys.METADATA_STORES_AMOUNT, data.storeAmount)
            .commit()
    }

    fun isSameData(serverData: SheetData): Boolean {
        val currentDataHash = sp.getInt(SPKeys.METADATA_CARDS_HASH, NONE)
        val serverDataHash = calcHash(serverData.cardItems)

        val currentStoresAmount = sp.getInt(SPKeys.METADATA_STORES_AMOUNT, NONE)
        val serverStoresAmount = serverData.storeAmount

        return (currentDataHash == serverDataHash) && (currentStoresAmount == serverStoresAmount)
    }

    private fun calcHash(items: List<SheetItem>): Int = items.hashCode()

    fun isMetadataExist(): Boolean = sp.contains(SPKeys.METADATA_CARDS_HASH)
//    fun isMetadataExist(): Boolean = false

    fun getCardsAmount(): Int = sp.getInt(SPKeys.METADATA_CARDS_AMOUNT, NONE)
    fun getStoresAmount(): Int = sp.getInt(SPKeys.METADATA_STORES_AMOUNT, NONE)

    companion object {
        private const val NONE = -1
    }
}