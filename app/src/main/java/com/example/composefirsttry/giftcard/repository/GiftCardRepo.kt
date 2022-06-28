package com.example.composefirsttry.giftcard.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.composefirsttry.giftcard.db.GiftCardDatabase
import com.example.composefirsttry.giftcard.db.dao.GiftCardDao
import com.example.composefirsttry.giftcard.db.entity.StoreEntity
import com.example.composefirsttry.giftcard.network.RestGiftCardService
import com.example.composefirsttry.giftcard.network.sheet.SheetItem
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GiftCardRepo @Inject constructor(
    db: GiftCardDatabase,
    private var restService: RestGiftCardService
) {
    private val giftCardDao: GiftCardDao = db.giftCardDao()
    private val allStores: LiveData<List<StoreEntity>> = giftCardDao.getAll()

    fun getAllStoresDb(): LiveData<List<StoreEntity>> = allStores

    @WorkerThread
    fun refresh() {
        fetchFromServer()
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

/*    fun insertData(context: Context, giftCardName: String, firstStoreName: String) {
        GlobalScope.launch(Dispatchers.IO) {
            db.giftCardDao().insertAll(
                giftCards = arrayListOf("zara", "fox").map { store -> StoreEntity(store) })
        }
    }*/
}