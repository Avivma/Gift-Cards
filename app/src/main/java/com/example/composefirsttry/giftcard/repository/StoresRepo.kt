package com.example.composefirsttry.giftcard.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.composefirsttry.giftcard.db.GiftCardDatabase
import com.example.composefirsttry.giftcard.db.dao.StoresDao
import com.example.composefirsttry.giftcard.db.entity.StoreEntity
import com.example.composefirsttry.giftcard.network.RestGiftCardService
import com.example.composefirsttry.giftcard.network.sheet.SheetItem
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoresRepo @Inject constructor(
    db: GiftCardDatabase,
    private var restService: RestGiftCardService
) {
    private val storesDao: StoresDao = db.storesDao()
    private val allStores: LiveData<List<StoreEntity>> = storesDao.getAll()

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
        val favoredStoresNames: List<String> = if (allStores.value != null) storesDao.getFavoriteStoresNames() else emptyList()

        storesDao.deleteAll()
        val storesDb: List<StoreEntity> = convert(storesServer)
        storesDao.insertAll(storesDb)

        if (favoredStoresNames.isNotEmpty()) storesDao.updateAsFavorites(favoredStoresNames)
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

    @WorkerThread
    fun addStoreToFavorites(store: StoreEntity) {
        storesDao.updateAsFavorites(listOf(store.storeName))
    }

    @WorkerThread
    fun resetFavorites() {
        storesDao.resetFavorites()
    }

    @WorkerThread
    fun removeStoreFromFavorites(storeName: String) {
        storesDao.removeStoreFromFavorites(storeName)
    }

/*    fun insertData(context: Context, giftCardName: String, firstStoreName: String) {
        GlobalScope.launch(Dispatchers.IO) {
            db.giftCardDao().insertAll(
                giftCards = arrayListOf("zara", "fox").map { store -> StoreEntity(store) })
        }
    }*/
}