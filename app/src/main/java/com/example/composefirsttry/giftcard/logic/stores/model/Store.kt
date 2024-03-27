package com.example.composefirsttry.giftcard.logic.stores.model

data class Store constructor(
    var storeName: String,
    var maxCard: Boolean = false,
    var corporateCard: Boolean = false,
    var hotCard: Boolean = false,
    var clubsAvailability: List<Boolean> = listOf(),
    var favorite: Boolean = false,
    var selected: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Store

        if (storeName != other.storeName) return false

        return true
    }

    override fun hashCode(): Int {
        return storeName.hashCode()
    }
}