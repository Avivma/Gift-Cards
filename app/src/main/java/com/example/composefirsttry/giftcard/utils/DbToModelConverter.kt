package com.example.composefirsttry.giftcard.utils

import com.example.composefirsttry.giftcard.logic.cards.db.entity.CardEntity
import com.example.composefirsttry.giftcard.logic.cards.model.CardSecureFields
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCard
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCardExtended
import com.example.composefirsttry.giftcard.logic.shoppingclubs.db.entity.ShoppingClubEntity
import com.example.composefirsttry.giftcard.logic.shoppingclubs.model.ShoppingClub
import com.example.composefirsttry.giftcard.logic.stores.db.entity.StoreEntity
import com.example.composefirsttry.giftcard.logic.stores.model.Store

object DbToModelConverter {
    fun fromEntityToStore(dbStore: StoreEntity): Store = Store(
        storeName = dbStore.storeName,
        maxCard = dbStore.maxCard,
        corporateCard = dbStore.corporateCard,
        hotCard = dbStore.hotCard,
        clubsAvailability = dbStore.clubsAvailability,
        favorite = dbStore.favorite
    )

    fun fromStoreToEntity(store: Store): StoreEntity = StoreEntity(
        storeName = store.storeName,
        storeNameHebrew = "",
        maxCard = store.maxCard,
        corporateCard = store.corporateCard,
        hotCard = store.hotCard,
        clubsAvailability = store.clubsAvailability,
        favorite = store.favorite
    )

    fun getGiftCardExtended(cardEntityWithEncryptedFields: CardEntity, secureFields: CardSecureFields.Values): GiftCardExtended {
        return GiftCardExtended(
            id = cardEntityWithEncryptedFields.cardId,
            cardClubId = cardEntityWithEncryptedFields.cardClubId,
            name = cardEntityWithEncryptedFields.name,
            imageUrl = cardEntityWithEncryptedFields.imageUrl,
            discount = cardEntityWithEncryptedFields.discount
        ).apply {
            number = secureFields.number
            cvv = secureFields.cvv
            expirationDate = secureFields.expirationDate
        }
    }

    fun getGiftCard(cardEntityWithEncryptedFields: CardEntity): GiftCard {
        return GiftCard(
            id = cardEntityWithEncryptedFields.cardId,
            cardClubId = cardEntityWithEncryptedFields.cardClubId,
            name = cardEntityWithEncryptedFields.name,
            imageUrl = cardEntityWithEncryptedFields.imageUrl,
            discount = cardEntityWithEncryptedFields.discount
        )
    }

    fun getCardEntity(giftCard: GiftCard, secureFields: CardSecureFields.Keys): CardEntity {
        return CardEntity(
            cardClubId = giftCard.cardClubId,
            name = giftCard.name,
            discount = giftCard.discount,
            imageUrl = giftCard.imageUrl,
            number = secureFields.number,
            cvv = secureFields.cvv,
            expirationDate = secureFields.expirationDate
        )
    }

    fun fromEntityToShoppingClub(clubEntity: ShoppingClubEntity): ShoppingClub {
        return ShoppingClub(
            clubId = clubEntity.clubId,
            index = clubEntity.index,
            type = clubEntity.type,
            imageUrl = clubEntity.imageUrl,
            loadMoneyAddress = clubEntity.loadMoneyAddress,
            loadThroughApp = clubEntity.loadThroughApp,
            checked = clubEntity.checked,
            hasAnyCards = clubEntity.count > 0,
        )
    }
}