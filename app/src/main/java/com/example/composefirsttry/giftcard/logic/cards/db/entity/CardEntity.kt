package com.example.composefirsttry.giftcard.logic.cards.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.composefirsttry.giftcard.logic.cards.db.entity.CardEntity.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class CardEntity constructor(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var Id: Int = 0,
    @ColumnInfo(name = "type")
    var type: Int,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "discount")
    var discount: Float,
    @ColumnInfo(name = "image")
    var imageRes: Int,
    @ColumnInfo(name = "longName")
    var longName: String,
    //IMPORTANT: all of the below are direct to Encrypted Shared Preference
    @ColumnInfo(name = "number")
    var number: String,
    @ColumnInfo(name = "cvv")
    var cvv: String,
    @ColumnInfo(name = "expirationDate")
    var expirationDate: String,
    @ColumnInfo(name = "creditCardNumber")
    var creditCardNumber: String,
    @ColumnInfo(name = "creditCardCvv")
    var creditCardCvv: String,
    @ColumnInfo(name = "creditCardExpirationDate")
    var creditCardExpirationDate: String
) {
    companion object {
        const val TABLE_NAME = "cards_table"
    }
}