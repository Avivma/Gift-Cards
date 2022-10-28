package com.example.composefirsttry.giftcard.ui.main.states

import com.example.composefirsttry.giftcard.logic.cards.model.GiftCardType
import com.example.composefirsttry.giftcard.logic.stores.model.Store

sealed class StoresMainIntention {
    //Filters
    data class FilterByPrefix(val prefix: String): StoresMainIntention()
    data class FilterByCard(val giftCardType: GiftCardType, val isChecked: Boolean): StoresMainIntention()
    object FilterBySelectedStores: StoresMainIntention()
    //Store Selection
    data class SelectStore(val store: Store): StoresMainIntention()
    object ClearStoresSelection: StoresMainIntention()
    //Favorites
    data class OpenStoreDialog(val store: Store) : StoresMainIntention()
    data class AddStoreToFavorites(val store: Store) : StoresMainIntention()
    //Navigation
    data class NavigateToCardsScreen(val giftCardType: GiftCardType): StoresMainIntention()

    data class CheckCardDiscount(val giftCardType: GiftCardType) : StoresMainIntention()
    object Refresh: StoresMainIntention()
}
