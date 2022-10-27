package com.example.composefirsttry.giftcard.ui.favorites.states

import com.example.composefirsttry.giftcard.logic.cards.model.GiftCardType
import com.example.composefirsttry.giftcard.logic.stores.model.Store

sealed class FavoritesState() {
    object Waiting : FavoritesState()
    data class DisplayData(val stores: List<Store>) : FavoritesState()
    data class StoreDialogOpened(val store: Store) : FavoritesState()
    object RemoveAllDialogOpened : FavoritesState()

    sealed class Navigation: FavoritesState() {
        data class NavigateToCardsScreen(val giftCardType: GiftCardType): FavoritesState.Navigation()
    }
}
