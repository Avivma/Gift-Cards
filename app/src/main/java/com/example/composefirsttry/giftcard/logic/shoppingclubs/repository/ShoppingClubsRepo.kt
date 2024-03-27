package com.example.composefirsttry.giftcard.logic.shoppingclubs.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.composefirsttry.giftcard.logic.GiftCardDatabase
import com.example.composefirsttry.giftcard.logic.metadata.network.sheet.SheetItem
import com.example.composefirsttry.giftcard.logic.shoppingclubs.db.dao.ShoppingClubDao
import com.example.composefirsttry.giftcard.logic.shoppingclubs.db.entity.ShoppingClubEntity
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShoppingClubsRepo @Inject constructor(
    db: GiftCardDatabase,
) {
    private val shoppingClubDao: ShoppingClubDao = db.shoppingClubDao()
    private val allShoppingClubs: LiveData<List<ShoppingClubEntity>> = shoppingClubDao.getAll()
    private val allExistingShoppingClubs: LiveData<List<ShoppingClubEntity>> = shoppingClubDao.getAllExistingClubs()

    fun getAllShoppingClubs(): LiveData<List<ShoppingClubEntity>> = allShoppingClubs
    fun getAllExistingShoppingClubs(): LiveData<List<ShoppingClubEntity>> = allExistingShoppingClubs

    fun initializeDb(metadataListServer: List<SheetItem>) {
        val previousShoppingClubs: List<ShoppingClubEntity> = shoppingClubDao.getAllAsList()

        shoppingClubDao.deleteAll()
        val shoppingClubs: List<ShoppingClubEntity> = convert(metadataListServer)
        shoppingClubDao.insertAll(shoppingClubs)

        shoppingClubDao.updateClubCheckedAndCounter(previousShoppingClubs)
    }

    private fun convert(metadataListServer: List<SheetItem>): List<ShoppingClubEntity> {
        val shoppingClubsDb: MutableList<ShoppingClubEntity> = ArrayList()
        shoppingClubsDb.addAll(metadataListServer.map { club ->
            ShoppingClubEntity(
                clubId = club.id,
                index = club.index.toInt()-1,
                type = club.type,
                imageUrl = club.imageUrl,
            )
        })
        return shoppingClubsDb
    }

    @WorkerThread
    fun updateShoppingClubChecked(clubId: String, checked: Boolean) {
        if (checked) {
            shoppingClubDao.updateAsChecked(clubId)
        } else {
            shoppingClubDao.removeClubFromChecked(clubId)
        }
    }

    @WorkerThread
    fun updateShoppingClubCounter(clubId: String, addition: Int) {
        shoppingClubDao.updateCounter(clubId, addition)
    }

    @WorkerThread
    fun getImageUrl(clubId: String): String {
        return shoppingClubDao.getImageUrl(clubId)
    }
}