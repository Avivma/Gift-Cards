package com.example.composefirsttry.giftcard.logic.shoppingclubs.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.composefirsttry.giftcard.logic.shoppingclubs.db.entity.ShoppingClubEntity

@Dao
interface ShoppingClubDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(clubs: List<ShoppingClubEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(club: ShoppingClubEntity)

    @Query("SELECT * FROM ${ShoppingClubEntity.TABLE_NAME}")
    fun getAll(): LiveData<List<ShoppingClubEntity>>

    @Query("SELECT * FROM ${ShoppingClubEntity.TABLE_NAME} WHERE count > 0")
    fun getAllExistingClubs(): LiveData<List<ShoppingClubEntity>>

    @Query("SELECT * FROM ${ShoppingClubEntity.TABLE_NAME}")
    fun getAllAsList(): List<ShoppingClubEntity>

    @Query("SELECT * FROM ${ShoppingClubEntity.TABLE_NAME} WHERE id LIKE :clubId")
    fun get(clubId: String): ShoppingClubEntity

    @Query("SELECT image_url FROM ${ShoppingClubEntity.TABLE_NAME} WHERE id LIKE :clubId")
    fun getImageUrl(clubId: String): String

    @Update
    fun update(club: ShoppingClubEntity)

    @Query("DELETE FROM ${ShoppingClubEntity.TABLE_NAME}")
    fun deleteAll()

    //Checked section:
    @Query("UPDATE ${ShoppingClubEntity.TABLE_NAME} SET checked = ${BOOLEAN_TRUE} WHERE id LIKE :clubId")
    fun updateAsChecked(clubId: String)

    @Query("UPDATE ${ShoppingClubEntity.TABLE_NAME} SET checked=${BOOLEAN_FALSE} WHERE id LIKE :clubId")
    fun removeClubFromChecked(clubId: String)

    //Count section:
    @Query("UPDATE ${ShoppingClubEntity.TABLE_NAME} SET count = count + :counter WHERE id LIKE :clubId")
    fun updateCounter(clubId: String, counter: Int)

    @Query("UPDATE ${ShoppingClubEntity.TABLE_NAME} SET checked = :checked, count = :counter WHERE id = :clubId")
    /*private*/ fun updateCheckAndCounterByIds(clubId: String, checked: Boolean, counter: Int)

    @Transaction
    fun updateClubCheckedAndCounter(shoppingClubs: List<ShoppingClubEntity>) {
        shoppingClubs.forEach { shoppingClub ->
            updateCheckAndCounterByIds(shoppingClub.clubId, shoppingClub.checked, shoppingClub.count)
        }
    }

    companion object {
        //the below values are based on the answer here: https://stackoverflow.com/a/47730858
        const val BOOLEAN_TRUE = 1
        const val BOOLEAN_FALSE = 0
    }
}