package com.example.composefirsttry.giftcard.logic

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.composefirsttry.giftcard.logic.cards.db.dao.CardsDao
import com.example.composefirsttry.giftcard.logic.cards.db.entity.CardEntity
import com.example.composefirsttry.giftcard.logic.shoppingclubs.db.dao.ShoppingClubDao
import com.example.composefirsttry.giftcard.logic.shoppingclubs.db.entity.ShoppingClubEntity
import com.example.composefirsttry.giftcard.logic.stores.db.converters.ShoppingClubConverters
import com.example.composefirsttry.giftcard.logic.stores.db.dao.StoresDao
import com.example.composefirsttry.giftcard.logic.stores.db.entity.StoreEntity

@Database(entities = [StoreEntity::class, CardEntity::class, ShoppingClubEntity::class], version = 3, exportSchema = false)
@TypeConverters(ShoppingClubConverters::class)
abstract class GiftCardDatabase : RoomDatabase() {

    abstract fun storesDao(): StoresDao

    abstract fun cardsDao(): CardsDao

    abstract fun shoppingClubDao(): ShoppingClubDao

    companion object {
        const val DB_NAME = "GIFT_CARD_STORE_DATABASE"
    }
}