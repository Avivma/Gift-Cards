package com.example.composefirsttry.giftcard.logic.stores.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "stores_table")
data class StoreEntity constructor(
    @PrimaryKey
    @ColumnInfo(name = "store_name")
    var storeName: String,
    @ColumnInfo(name = "store_name_hebrew")
    var storeNameHebrew: String,
    @ColumnInfo(name = "max_card")
    var maxCard: Boolean = false,
    @ColumnInfo(name = "corporate_card")
    var corporateCard: Boolean = false,
    @ColumnInfo(name = "hot_card")
    var hotCard: Boolean = false,
    @ColumnInfo(name = "clubs_availability")
    var clubsAvailability: List<Boolean> = listOf(),
    @ColumnInfo(name = "favorite")
    var favorite: Boolean = false
) {
    override fun toString(): String {
        return "StoreEntity(storeName='$storeName', storeNameHebrew='$storeNameHebrew', maxCard=$maxCard, corporateCard=$corporateCard, hotCard=$hotCard, clubsAvailability=$clubsAvailability, favorite=$favorite)"
    }
}
