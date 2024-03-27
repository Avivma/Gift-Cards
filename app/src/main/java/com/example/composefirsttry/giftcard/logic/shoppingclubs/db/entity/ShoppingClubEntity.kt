package com.example.composefirsttry.giftcard.logic.shoppingclubs.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = ShoppingClubEntity.TABLE_NAME)
data class ShoppingClubEntity constructor(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var clubId: String,
    @ColumnInfo(name = "index")
    var index: Int,
    @ColumnInfo(name = "type")
    var type: String,
    @ColumnInfo(name = "image_url")
    var imageUrl: String,
    @ColumnInfo(name = "checked")
    var checked: Boolean = true,
    @ColumnInfo(name = "count")
    var count: Int = 0,
) {
    override fun toString(): String {
        return "ShoppingClubTypeEntity(clubId=${clubId}, index=${index}, type=${type}, imageUrl=${imageUrl}, checked=${checked}, count=${count})"
    }

    companion object {
        const val TABLE_NAME = "shopping_club_table"
    }
}