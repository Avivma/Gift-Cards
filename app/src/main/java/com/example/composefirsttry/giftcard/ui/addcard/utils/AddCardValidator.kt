package com.example.composefirsttry.giftcard.ui.addcard.utils

import com.example.composefirsttry.giftcard.logic.cards.model.CardFieldStatus
import com.example.composefirsttry.giftcard.logic.cards.model.CardFieldType
import com.example.composefirsttry.giftcard.logic.cards.model.FieldWithError
import com.example.composefirsttry.giftcard.logic.cards.model.FieldWithPartialError
import java.util.*

class AddCardValidator {
    private val fieldsStatusMap: HashMap<CardFieldType, CardFieldStatus> = hashMapOf()

    init {
        fieldsStatusMap[CardFieldType.Image] = FieldWithError(statusOk = true)
        fieldsStatusMap[CardFieldType.Name] = FieldWithError(statusOk = true)
        fieldsStatusMap[CardFieldType.Discount] = FieldWithError(statusOk = true)
        fieldsStatusMap[CardFieldType.Number] = FieldWithPartialError(statusOk = true)
        fieldsStatusMap[CardFieldType.Cvv] = FieldWithPartialError(statusOk = true)
        fieldsStatusMap[CardFieldType.ExpirationDate] = FieldWithPartialError(statusOk = true)
    }

    fun getFieldsStatusMap(): Map<CardFieldType, CardFieldStatus> {
        val pairs = mutableListOf<Pair<CardFieldType, CardFieldStatus>>()
        for (entry in fieldsStatusMap) {
            pairs.add(Pair(entry.key, entry.value))
        }
        return mapOf(*pairs.toTypedArray())
    }

    fun validateCardFields(fieldsValueMap: HashMap<CardFieldType, String>) {
        for (entry in fieldsValueMap) {
            val fieldStatus: Boolean = isFieldOk(entry.key, entry.value)
            fieldsStatusMap.getValue(entry.key).setFieldStatusOk(fieldStatus)
        }
    }

    private fun isFieldOk(type: CardFieldType, fieldValue: String): Boolean {
        return when (type) {
            CardFieldType.Image -> isBooleanFieldOk(fieldValue)
            CardFieldType.Name -> isStringFieldOk(fieldValue)
            CardFieldType.Discount -> isFloatFieldOk(fieldValue)
            CardFieldType.Number -> isStringFieldOk(fieldValue)
            CardFieldType.Cvv -> isStringFieldOk(fieldValue)
            CardFieldType.ExpirationDate -> isStringFieldOk(fieldValue)
        }
    }

    private fun isFloatFieldOk(fieldValue: String): Boolean = fieldValue.toFloatOrNull() != null

    private fun isBooleanFieldOk(fieldValue: String): Boolean = fieldValue.toBoolean()

    private fun isStringFieldOk(fieldValue: String): Boolean = fieldValue.isNotEmpty()

    fun isCardDetailsOk(): Boolean = fieldsStatusMap.values.all { it.isFieldStatusOk() }

    fun isCardDetailsPartialFailed(): Boolean {
        val values = fieldsStatusMap.values
        return values.any { it.isFieldStatusPartialFailed() } && values.none { it.isFieldStatusFailed() }
    }

    fun isCardDetailsFailed(): Boolean = fieldsStatusMap.values.any { it.isFieldStatusFailed() }

    fun isFieldStatusOk(fieldType: CardFieldType): Boolean = fieldsStatusMap.getValue(fieldType).isFieldStatusOk()
}

