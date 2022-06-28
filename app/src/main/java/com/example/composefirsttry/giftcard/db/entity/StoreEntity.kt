package com.example.composefirsttry.giftcard.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gift_card_stores")
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
    var hotCard: Boolean = false
) {
    //    @PrimaryKey(autoGenerate = true)
//    @ColumnInfo(name = "id")
//    var Id: Int = 0
    override fun toString(): String {
        return "StoreEntity(storeName='$storeName', storeNameHebrew='$storeNameHebrew', maxCard=$maxCard, corporateCard=$corporateCard, hotCard=$hotCard)"
    }
}
