package com.example.composefirsttry.giftcard.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
//import com.example.giftcardsfun.db.converter.GiftCardsConverter
import com.example.composefirsttry.giftcard.db.dao.GiftCardDao
import com.example.composefirsttry.giftcard.db.entity.StoreEntity

@Database(entities = [StoreEntity::class], version = 1, exportSchema = false)
//@TypeConverters(GiftCardsConverter::class)
abstract class GiftCardDatabase : RoomDatabase() {

    abstract fun giftCardDao(): GiftCardDao

    companion object {

        @Volatile
        private var INSTANCE: GiftCardDatabase? = null

        fun getDataseClient(context: Context): GiftCardDatabase {

            if (INSTANCE != null) return INSTANCE!!

            synchronized(this) {

                INSTANCE = Room
                    .databaseBuilder(context, GiftCardDatabase::class.java, "GIFT_CARD_STORE_DATABASE")
//                    .addTypeConverter(GiftCardsConverter::class)
                    .fallbackToDestructiveMigration()
                    .build()

                return INSTANCE!!
            }
        }
    }
}