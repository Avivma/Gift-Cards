package com.example.composefirsttry.giftcard.logic.cards.repository

import android.content.SharedPreferences
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.composefirsttry.di.EncryptedSp
import com.example.composefirsttry.giftcard.logic.GiftCardDatabase
import com.example.composefirsttry.giftcard.logic.cards.db.dao.CardsDao
import com.example.composefirsttry.giftcard.logic.cards.db.entity.CardEntity
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCardExtended
import com.example.composefirsttry.giftcard.logic.shoppingclubs.repository.ShoppingClubsRepo
import com.example.composefirsttry.giftcard.utils.DbToModelConverter
import com.example.composefirsttry.utils.SPKeys
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardsRepo @Inject constructor(
    db: GiftCardDatabase,
    private var shoppingClubsRepo: ShoppingClubsRepo,
    private val sp: SharedPreferences,
    @EncryptedSp private val encryptedSP: SharedPreferences,
    private val encryptionHandler: CardEncryptionHandler
) {
    private val cardsDao: CardsDao = db.cardsDao()
    private val allCards: LiveData<List<CardEntity>> = cardsDao.getAll()

    fun getAllCardsDb(): LiveData<List<CardEntity>> = allCards

    fun hasAnyCard(): Boolean = sp.getInt(SPKeys.GIFT_CARDS_TOTAL_AMOUNT, 0) > 0

    @WorkerThread
    fun addCard(giftCard: GiftCardExtended) {
        val keys = encryptionHandler.getEncryptedKeys(giftCard)
        //add card plain data
        cardsDao.insert(DbToModelConverter.getCardEntity(giftCard, keys))
        //add card secure data
        encryptedSP.edit()
            .putString(keys.number, giftCard.number)
            .putString(keys.cvv, giftCard.cvv)
            .putString(keys.expirationDate, giftCard.expirationDate)
            .commit()
        //update whether there is inserted card (this update is only for condition in Activity)
        updateCardsAmount(giftCard.cardClubId, 1)
    }

    @WorkerThread
    fun editCard(giftCard: GiftCardExtended) {
        val keys = encryptionHandler.getEncryptedKeys(giftCard)
        val cardEntityPreviousClubId = cardsDao.getCardDetails(giftCard.id).cardClubId
        //add card plain data
        val cardEntity = DbToModelConverter.getCardEntity(giftCard, keys).apply { cardId = giftCard.id }
        cardsDao.update(cardEntity)
        //add card secure data
        encryptedSP.edit()
            .putString(keys.number, giftCard.number)
            .putString(keys.cvv, giftCard.cvv)
            .putString(keys.expirationDate, giftCard.expirationDate)
            .commit()
        //update whether there is card change (this update is only for condition in Activity)
        if (cardEntity.cardClubId != cardEntityPreviousClubId) {
            updateCardsAmount(cardEntity.cardClubId, 1)
            updateCardsAmount(cardEntityPreviousClubId , -1)
        }
    }

    @WorkerThread
    fun removeCard(cardId: Int) {
        val giftCard = getCard(cardId)
        val keys = encryptionHandler.getEncryptedKeys(giftCard)
        //remove card plain data
        cardsDao.remove(cardId)
        //remove card secure data
        encryptedSP.edit()
            .remove(keys.number)
            .remove(keys.cvv)
            .remove(keys.expirationDate)
            .commit()
        //update whether there is inserted card (this update is only for condition in Activity)
        updateCardsAmount(giftCard.cardClubId, -1)
    }

    @WorkerThread
    fun getCard(cardId: Int): GiftCardExtended {
        val cardEntity = cardsDao.getCardDetails(cardId)
        val values = encryptionHandler.getDecryptedValues(cardEntity)
        return DbToModelConverter.getGiftCardExtended(cardEntity, values)
    }

    private fun updateCardsAmount(cardClubId: String, addition: Int) {
        shoppingClubsRepo.updateShoppingClubCounter(cardClubId, addition)
        updateCardsTotalAmount(addition)
    }

    private fun updateCardsTotalAmount(addition: Int) {
        val currentTotalAmount = sp.getInt(SPKeys.GIFT_CARDS_TOTAL_AMOUNT, 0)
        sp.edit().putInt(SPKeys.GIFT_CARDS_TOTAL_AMOUNT, currentTotalAmount + addition).commit()
    }
}