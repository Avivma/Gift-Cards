package com.example.composefirsttry.giftcard.ui.main.states

import com.example.composefirsttry.giftcard.model.GiftCard
import com.example.composefirsttry.giftcard.model.Store

sealed class StoresMainIntention {
    data class FilterByPrefix(val prefix: String): StoresMainIntention()
    data class FilterByCard(val card: GiftCard, val isChecked: Boolean): StoresMainIntention()
    object FilterBySelectedStores: StoresMainIntention()
    data class NavigateToCardsScreen(val card: GiftCard): StoresMainIntention()
    data class SelectStore(val store: Store): StoresMainIntention()
    object ClearStoresSelection: StoresMainIntention()
    object Refresh: StoresMainIntention()
}
