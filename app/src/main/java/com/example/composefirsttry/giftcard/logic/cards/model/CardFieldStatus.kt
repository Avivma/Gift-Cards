package com.example.composefirsttry.giftcard.logic.cards.model

abstract class CardFieldStatus(protected var status: Boolean) {
    fun isFieldStatusOk(): Boolean = status
    fun setFieldStatusOk(status: Boolean) { this.status = status }
    abstract fun isFieldStatusFailed(): Boolean
    abstract fun isFieldStatusPartialFailed(): Boolean
}

class FieldWithError(statusOk: Boolean) : CardFieldStatus(statusOk) {
    override fun isFieldStatusFailed(): Boolean = !status
    override fun isFieldStatusPartialFailed(): Boolean = false
}

class FieldWithPartialError(statusOk: Boolean) : CardFieldStatus(statusOk) {
    override fun isFieldStatusFailed(): Boolean = false
    override fun isFieldStatusPartialFailed(): Boolean = !status
}