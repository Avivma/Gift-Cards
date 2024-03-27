package com.example.composefirsttry.giftcard.logic.shoppingclubs.model


data class ShoppingClub constructor(
    var clubId: String,
    var index: Int,
    var type: String,
    var imageUrl: String,
    var checked: Boolean,
    var hasAnyCards: Boolean,
)
{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShoppingClub

        if (clubId != other.clubId) return false

        return true
    }

    override fun hashCode(): Int {
        return clubId.hashCode()
    }
}