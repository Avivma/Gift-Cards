package com.example.composefirsttry.giftcard.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.composefirsttry.giftcard.db.entity.StoreEntity

@Dao
interface GiftCardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(giftCards: List<StoreEntity>)

    @Query("SELECT * FROM gift_card_stores")
    fun getAll(): LiveData<List<StoreEntity>>

    @Query("SELECT * FROM gift_card_stores WHERE store_name LIKE :searchedPrefix || '%'")
    fun getStores(searchedPrefix: String): LiveData<List<StoreEntity>>

    @Query("UPDATE gift_card_stores SET favorite = $BOOLEAN_TRUE WHERE store_name IN (:storesNames)")
    fun addToFavorites(storesNames: List<String>)

    @Query("UPDATE gift_card_stores SET favorite=$BOOLEAN_FALSE")
    fun resetFavorites()

    @Query("SELECT store_name FROM gift_card_stores WHERE favorite=$BOOLEAN_TRUE")
    fun getFavoriteStoresNames(): List<String>

    @Query("DELETE FROM gift_card_stores")
    fun deleteAll()

    companion object {
        //the below values are based on the answer here: https://stackoverflow.com/a/47730858
        const val BOOLEAN_TRUE = 1
        const val BOOLEAN_FALSE = 0
    }
}