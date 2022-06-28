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

    @Query("DELETE FROM gift_card_stores")
    fun deleteAll()
}