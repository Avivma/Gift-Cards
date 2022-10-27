package com.example.composefirsttry.giftcard.logic.cards.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.composefirsttry.giftcard.logic.cards.db.entity.CardEntity

@Dao
interface CardsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(card: CardEntity)

    @Query("SELECT * FROM ${CardEntity.TABLE_NAME}")
    fun getAll(): LiveData<List<CardEntity>>

    @Query("SELECT * FROM ${CardEntity.TABLE_NAME} WHERE type=:type AND name LIKE :name")
    fun getCardDetails(type: Int, name: String): CardEntity

    @Delete
    fun remove(card: CardEntity)

    @Query("DELETE FROM ${CardEntity.TABLE_NAME}")
    fun deleteAll()
}