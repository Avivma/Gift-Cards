package com.example.composefirsttry.giftcard.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.composefirsttry.giftcard.db.dao.GiftCardDao
import com.example.composefirsttry.giftcard.db.entity.StoreEntity

@Database(entities = [StoreEntity::class], version = 1, exportSchema = false)
abstract class GiftCardDatabase : RoomDatabase() {

    abstract fun giftCardDao(): GiftCardDao

    companion object {
        const val DB_NAME = "GIFT_CARD_STORE_DATABASE"
    }
}