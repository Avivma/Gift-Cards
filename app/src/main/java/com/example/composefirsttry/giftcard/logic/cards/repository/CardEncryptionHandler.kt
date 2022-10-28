package com.example.composefirsttry.giftcard.logic.cards.repository

import android.content.SharedPreferences
import com.example.composefirsttry.di.EncryptedSp
import com.example.composefirsttry.giftcard.logic.cards.db.entity.CardEntity
import com.example.composefirsttry.giftcard.logic.cards.model.CardSecureFields
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCardExtended
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class CardEncryptionHandler @Inject constructor(
    @EncryptedSp private val encryptedSP: SharedPreferences
) {
    fun getDecryptedValues(cardEntityWithEncryptedFields: CardEntity): CardSecureFields.Values {
        return CardSecureFields.Values(
            number = encryptedSP.getString(cardEntityWithEncryptedFields.number, "") ?: "",
            cvv = encryptedSP.getString(cardEntityWithEncryptedFields.cvv, "") ?: "",
            expirationDate = encryptedSP.getString(cardEntityWithEncryptedFields.expirationDate, "") ?: ""
        )
    }

    fun getEncryptedKeys(giftCard: GiftCardExtended): CardSecureFields.Keys {
        return CardSecureFields.Keys(
            number = createSpKey("number", giftCard.number),
            cvv = createSpKey("cvv", giftCard.cvv),
            expirationDate = createSpKey("expirationDate", giftCard.expirationDate)
        )
    }

    private fun createSpKey(prefix: String, data: String): String {
        return "${prefix}-${data}-${System.currentTimeMillis()}".hashCode().toString()
    }

    /*    fun getGiftCard(cardEntityWithEncryptedFields: CardEntity): GiftCard {
        return GiftCard(
            type = GiftCardType.getCardType(cardEntityWithEncryptedFields.type),
            name = cardEntityWithEncryptedFields.name
        ).apply {
            discount = cardEntityWithEncryptedFields.discount
            imageRes = cardEntityWithEncryptedFields.imageRes
            number = encryptedSP.getString(cardEntityWithEncryptedFields.number, "") ?: ""
            cvv = encryptedSP.getString(cardEntityWithEncryptedFields.cvv, "") ?: ""
            expirationDate = encryptedSP.getString(cardEntityWithEncryptedFields.expirationDate, "") ?: ""
        }
    }

    fun getCardEntity(giftCard: GiftCard): CardEntity {
        val cardNumberKey = createSpKey()
        val cardCvvKey = createSpKey()
        val cardExpirationDateKey = createSpKey()
        encryptedSP.edit()
            .putString(cardNumberKey, giftCard.number)
            .putString(cardCvvKey, giftCard.cvv)
            .putString(cardExpirationDateKey, giftCard.expirationDate)
            .commit()
        return CardEntity(
            type = giftCard.type.value,
            name = giftCard.name,
            discount = giftCard.discount,
            imageRes = giftCard.imageRes,
            number = cardNumberKey,
            cvv = cardCvvKey,
            expirationDate = cardExpirationDateKey
        )
    }*/
}

