package com.example.composefirsttry.giftcard.ui.main.states

import com.example.composefirsttry.giftcard.model.GiftCard

sealed class StoresMainIntention {
    data class FilterByPrefix(val prefix: String): StoresMainIntention()
    data class FilterByCard(val card: GiftCard, val isChecked: Boolean): StoresMainIntention()
    object Refresh: StoresMainIntention()
}
