package com.example.composefirsttry.giftcard.logic.stores.repository

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.composefirsttry.giftcard.logic.shoppingclubs.db.entity.ShoppingClubEntity
import com.example.composefirsttry.giftcard.logic.shoppingclubs.repository.ShoppingClubsRepo
import com.example.composefirsttry.giftcard.logic.stores.db.entity.StoreEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoresConsiderCardsRepo @Inject constructor(
    private var storesRepo: StoresRepo,
    private var shoppingClubsRepo: ShoppingClubsRepo,
) {
    private val storesMediatorLiveData: MediatorLiveData<List<StoreEntity>> = MediatorLiveData()
    fun getAllStoresCache(): LiveData<List<StoreEntity>> = storesMediatorLiveData

    init {
        initListeners()
    }

    @MainThread
    private fun initListeners() {
        storesMediatorLiveData.addSource(storesRepo.getAllStoresDb()) { stores ->
            storesMediatorLiveData.value = filterStoresByCardsExistence(stores)
        }

        storesMediatorLiveData.addSource(shoppingClubsRepo.getAllExistingShoppingClubs()) {
            val stores = getStores()
            storesMediatorLiveData.value = filterStoresByCardsExistence(stores)
        }
    }

    private fun filterStoresByCardsExistence(stores: List<StoreEntity>): List<StoreEntity> {
        return stores.filter { doesStoreCardsExist(it) }
    }

    private fun doesStoreCardsExist(store: StoreEntity): Boolean {
        val shoppingClubs = getShoppingClubs()
        if (shoppingClubs.isEmpty()) return false

        return shoppingClubs.any { club -> store.clubsAvailability[club.index] }
    }


    private fun getStores(): List<StoreEntity> = storesRepo.getAllStoresDb().value ?: emptyList()

    private fun getShoppingClubs(): List<ShoppingClubEntity> = shoppingClubsRepo.getAllExistingShoppingClubs().value ?: emptyList()
}