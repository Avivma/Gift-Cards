package com.example.composefirsttry.giftcard.logic.stores.repository

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCardType
import com.example.composefirsttry.giftcard.logic.cards.repository.CardUtils
import com.example.composefirsttry.giftcard.logic.cards.repository.CardsRepo
import com.example.composefirsttry.giftcard.logic.stores.db.entity.StoreEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoresConsiderCardsRepo @Inject constructor(
    private var storesRepo: StoresRepo,
    private var cardsRepo: CardsRepo,
    private var cardUtils: CardUtils
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

        storesMediatorLiveData.addSource(cardsRepo.getAllCardsDb()) {
            val stores = getStores()
            storesMediatorLiveData.value = filterStoresByCardsExistence(stores)
        }
    }

    private fun filterStoresByCardsExistence(stores: List<StoreEntity>): List<StoreEntity> {
        return stores.filter { doesStoreCardsExist(it) }
    }

    private fun doesStoreCardsExist(store: StoreEntity): Boolean =
        ((cardUtils.hasCard(GiftCardType.MAX) && store.maxCard) ||
        (cardUtils.hasCard(GiftCardType.ISRACARD) && store.corporateCard) ||
        (cardUtils.hasCard(GiftCardType.TAV_HAHAM) && store.hotCard))

    private fun getStores(): List<StoreEntity> = storesRepo.getAllStoresDb().value ?: emptyList()
}