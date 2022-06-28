package com.example.composefirsttry.giftcard.utils

import com.example.composefirsttry.giftcard.db.entity.StoreEntity
import com.example.composefirsttry.giftcard.model.Store

object DbToModelConverter {
    fun fromEntityToStore(dbStore: StoreEntity): Store = Store(dbStore.storeName, maxCard = dbStore.maxCard , corporateCard = dbStore.corporateCard, hotCard = dbStore.hotCard)
}