package com.example.composefirsttry.giftcard.ui.favorites.states

import com.example.composefirsttry.giftcard.logic.stores.model.Store
import com.example.composefirsttry.giftcard.ui.common.model.ClubIdAndUrl

sealed class FavoritesState {
    object Waiting : FavoritesState()
    data class DisplayData(val storesAndClubs: Map<Store, List<ClubIdAndUrl>>, val showDeleteAll: Boolean) : FavoritesState()
    data class StoreDialogOpened(val store: Store) : FavoritesState()
    object RemoveAllDialogOpened : FavoritesState()

    sealed class Navigation: FavoritesState() {
        data class NavigateToCardsScreen(val shoppingClubId: String): FavoritesState.Navigation()
    }
}
