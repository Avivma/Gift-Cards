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

    @Query("SELECT * FROM ${CardEntity.TABLE_NAME} WHERE id=:cardId")
    fun getCardDetails(cardId: Int): CardEntity

    @Update
    fun update(card: CardEntity)

    @Query("DELETE FROM ${CardEntity.TABLE_NAME} WHERE id=:cardId")
    fun remove(cardId: Int)

    @Query("DELETE FROM ${CardEntity.TABLE_NAME}")
    fun deleteAll()
}