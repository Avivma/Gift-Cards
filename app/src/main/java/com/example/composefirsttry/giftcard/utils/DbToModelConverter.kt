package com.example.composefirsttry.giftcard.utils

import com.example.composefirsttry.giftcard.logic.stores.db.entity.StoreEntity
import com.example.composefirsttry.giftcard.logic.stores.model.Store

object DbToModelConverter {
    fun fromEntityToStore(dbStore: StoreEntity): Store = Store(
        storeName = dbStore.storeName,
        maxCard = dbStore.maxCard,
        corporateCard = dbStore.corporateCard,
        hotCard = dbStore.hotCard,
        favorite = dbStore.favorite
    )

    fun fromStoreToEntity(store: Store): StoreEntity = StoreEntity(
        storeName = store.storeName,
        storeNameHebrew = "",
        maxCard = store.maxCard,
        corporateCard = store.corporateCard,
        hotCard = store.hotCard,
        favorite = store.favorite
    )
}