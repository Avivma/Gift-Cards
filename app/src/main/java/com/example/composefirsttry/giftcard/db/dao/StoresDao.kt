package com.example.composefirsttry.giftcard.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.composefirsttry.giftcard.db.entity.StoreEntity
import com.example.composefirsttry.giftcard.model.GiftCardType

@Dao
interface StoresDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(giftCards: List<StoreEntity>)

    @Query("SELECT * FROM stores_table")
    fun getAll(): LiveData<List<StoreEntity>>

    @Query("SELECT * FROM stores_table WHERE store_name LIKE :searchedPrefix || '%'")
    fun getStores(searchedPrefix: String): LiveData<List<StoreEntity>>

    @Query("SELECT * FROM stores_table WHERE (max_card=$BOOLEAN_TRUE AND ${GiftCardType.MAX_CARD_VALUE} IN (:giftCardsTypeValue)) OR (corporate_card=$BOOLEAN_TRUE AND ${GiftCardType.ISRACARD_CARD_VALUE} IN (:giftCardsTypeValue)) OR (hot_card=$BOOLEAN_TRUE AND ${GiftCardType.TAV_HAHAM_CARD_VALUE} IN (:giftCardsTypeValue))")
    fun getStores(giftCardsTypeValue: List<Int>): List<StoreEntity>

    @Query("UPDATE stores_table SET favorite = $BOOLEAN_TRUE WHERE store_name IN (:storesNames)")
    fun updateAsFavorites(storesNames: List<String>)

    @Query("UPDATE stores_table SET favorite=$BOOLEAN_FALSE WHERE store_name LIKE :storeName")
    fun removeStoreFromFavorites(storeName: String)

    @Query("UPDATE stores_table SET favorite=$BOOLEAN_FALSE")
    fun resetFavorites()

    @Query("SELECT store_name FROM stores_table WHERE favorite=$BOOLEAN_TRUE")
    fun getFavoriteStoresNames(): List<String>

    @Query("DELETE FROM stores_table")
    fun deleteAll()

    companion object {
        //the below values are based on the answer here: https://stackoverflow.com/a/47730858
        const val BOOLEAN_TRUE = 1
        const val BOOLEAN_FALSE = 0
    }
}