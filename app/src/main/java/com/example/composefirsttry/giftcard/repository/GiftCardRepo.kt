package com.example.composefirsttry.giftcard.repository

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.composefirsttry.giftcard.db.GiftCardDatabase
import com.example.composefirsttry.giftcard.db.dao.GiftCardDao
import com.example.composefirsttry.giftcard.db.entity.StoreEntity
import com.example.composefirsttry.giftcard.network.RestGiftCardService
import com.example.composefirsttry.giftcard.network.sheet.SheetItem
import java.util.*

object GiftCardRepo {
    private lateinit var db: GiftCardDatabase
    private lateinit var giftCardDao: GiftCardDao
    private lateinit var allStores: LiveData<List<StoreEntity>>
    private lateinit var restService: RestGiftCardService

    fun getAllStoresDb(): LiveData<List<StoreEntity>> = allStores

    @WorkerThread
    fun initializeDB(context: Context) {
        db = GiftCardDatabase.getDataseClient(context)
        giftCardDao = db.giftCardDao()
        allStores = giftCardDao.getAll()
        restService = RestGiftCardService()

        if ((allStores.value?.size ?: 0) == 0) {
            fetchFromServer()
        }
    }

    private fun fetchFromServer() {
        val storesServer: List<SheetItem> = restService.getStores()
        mergeToDb(storesServer)
    }

    private fun mergeToDb(storesServer: List<SheetItem>) {
        giftCardDao.deleteAll()
        val giftCards: List<StoreEntity> = convert(storesServer)
        giftCardDao.insertAll(giftCards)
    }

    private fun convert(storesServer: List<SheetItem>): List<StoreEntity> {
        val storesDb: MutableList<StoreEntity> = ArrayList()
        storesDb.addAll(storesServer.map { store ->
            StoreEntity(
                storeName = store.storeName,
                storeNameHebrew = "",
                maxCard = store.maxAvailability == "V",
                corporateCard = store.corporateAvailability == "V",
                hotCard = store.hotAvailability == "V"
            )
        })
        return storesDb
    }

//    fun insertData(context: Context, giftCardName: String, firstStoreName: String) {
//        GlobalScope.launch(Dispatchers.IO) {
//            db.giftCardDao().insertAll(
//                giftCards = arrayListOf("zara", "fox").map { store -> StoreEntity(store) })
//        }
//    }

    @WorkerThread
    suspend fun refresh() {
        fetchFromServer()
    }
}