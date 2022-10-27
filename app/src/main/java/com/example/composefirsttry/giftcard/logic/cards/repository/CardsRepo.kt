package com.example.composefirsttry.giftcard.logic.cards.repository

import android.content.SharedPreferences
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.composefirsttry.di.EncryptedSp
import com.example.composefirsttry.giftcard.logic.GiftCardDatabase
import com.example.composefirsttry.giftcard.logic.cards.db.dao.CardsDao
import com.example.composefirsttry.giftcard.logic.cards.db.entity.CardEntity
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCard
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCardPlainInfo
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCardType
import com.example.composefirsttry.giftcard.utils.DbToModelConverter
import com.example.composefirsttry.utils.SPKeys
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardsRepo @Inject constructor(
    db: GiftCardDatabase,
    private val sp: SharedPreferences,
    @EncryptedSp private val encryptedSP: SharedPreferences,
    private val encryptionHandler: CardEncryptionHandler
) {
    private val cardsDao: CardsDao = db.cardsDao()
    private val allCards: LiveData<List<CardEntity>> = cardsDao.getAll()

    fun getAllCardsDb(): LiveData<List<CardEntity>> = allCards

    private val cardsExistMutableLiveData = MutableLiveData<Boolean>()
    val getCardsExistLiveData: LiveData<Boolean> = cardsExistMutableLiveData

//    @WorkerThread
//    fun getAllCardsPlainDataOnly(): List<CardEntity> = allCards.value!!

/*    @WorkerThread
    fun addCard(card: CardEntity) {
        cardsDao.insert(card)
    }

    @WorkerThread
    fun editCard(card: CardEntity) {
        addCard(card) //perform "update" in case of conflict: OnConflictStrategy.REPLACE
    }

    @WorkerThread
    fun removeCard(card: CardEntity) {
        cardsDao.remove(card)
    }

    @WorkerThread
    fun getCard(cardKey: CardDbKey): CardEntity {
        return cardsDao.getCardDetails(cardKey.type.value, cardKey.name)
    }*/

    @WorkerThread
    fun addCard(giftCard: GiftCard) {
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
        updateCardsAmount(giftCard, 1)
    }

    @WorkerThread
    fun editCard(giftCard: GiftCard) {
        addCard(giftCard) //perform "update" in case of conflict: OnConflictStrategy.REPLACE
    }

    @WorkerThread
    fun removeCard(giftCard: GiftCard) {
        val keys = encryptionHandler.getEncryptedKeys(giftCard)
        //remove card plain data
        cardsDao.remove(DbToModelConverter.getCardEntity(giftCard, keys))
        //remove card secure data
        encryptedSP.edit()
            .remove(keys.number)
            .remove(keys.cvv)
            .remove(keys.expirationDate)
            .commit()
        //update whether there is inserted card (this update is only for condition in Activity)
        updateCardsAmount(giftCard, -1)
    }

    @WorkerThread
    fun getCard(cardPlainInfo: GiftCardPlainInfo): GiftCard {
        val cardEntity = cardsDao.getCardDetails(cardPlainInfo.type.value, cardPlainInfo.name)
        val values = encryptionHandler.getDecryptedValues(cardEntity)
        return DbToModelConverter.getGiftCard(cardEntity, values)
    }

    private fun updateCardsAmount(giftCard: GiftCard, addition: Int) {
        val (cardSpKey, specificCardAmount) = updateSpecificCardsAmount(giftCard.type, addition)
        val totalCardsAmount = sp.getInt(SPKeys.GIFT_CARD_AMOUNT_CARDS_INSERTED, 0) + addition
        sp.edit().putInt(SPKeys.GIFT_CARD_AMOUNT_CARDS_INSERTED, totalCardsAmount)
            .putInt(cardSpKey, specificCardAmount)
            .commit()
        cardsExistMutableLiveData.postValue(totalCardsAmount > 0)
    }

    private fun updateSpecificCardsAmount(type: GiftCardType, addition: Int): Pair<String, Int> {
        return when (type) {
            GiftCardType.MAX -> Pair(
                SPKeys.GIFT_CARD_AMOUNT_MAX_CARDS,
                sp.getInt(SPKeys.GIFT_CARD_AMOUNT_MAX_CARDS, 0) + addition
            )
            GiftCardType.ISRACARD -> Pair(
                SPKeys.GIFT_CARD_AMOUNT_ISRACARD_CARDS,
                sp.getInt(SPKeys.GIFT_CARD_AMOUNT_ISRACARD_CARDS, 0) + addition
            )
            GiftCardType.TAV_HAHAM -> Pair(
                SPKeys.GIFT_CARD_AMOUNT_TAV_HAHAM_CARDS,
                sp.getInt(SPKeys.GIFT_CARD_AMOUNT_TAV_HAHAM_CARDS, 0) + addition
            )
        }
    }
}