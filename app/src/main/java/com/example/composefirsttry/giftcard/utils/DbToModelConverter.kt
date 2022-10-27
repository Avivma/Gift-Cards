package com.example.composefirsttry.giftcard.utils

import com.example.composefirsttry.giftcard.logic.cards.db.entity.CardEntity
import com.example.composefirsttry.giftcard.logic.cards.model.CardSecureFields
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCard
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCardType
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

    fun getGiftCard(cardEntityWithEncryptedFields: CardEntity, secureFields: CardSecureFields.Values): GiftCard {
        return GiftCard(
            type = GiftCardType.getCardType(cardEntityWithEncryptedFields.type),
            name = cardEntityWithEncryptedFields.name,
            imageRes = cardEntityWithEncryptedFields.imageRes,
            discount = cardEntityWithEncryptedFields.discount
        ).apply {
            number = secureFields.number
            cvv = secureFields.cvv
            expirationDate = secureFields.expirationDate
        }
    }

    fun getCardEntity(giftCard: GiftCard, secureFields: CardSecureFields.Keys): CardEntity {
        return CardEntity(
            type = giftCard.type.value,
            name = giftCard.name,
            discount = giftCard.discount,
            imageRes = giftCard.imageRes,
            number = secureFields.number,
            cvv = secureFields.cvv,
            expirationDate = secureFields.expirationDate
        )
    }
}