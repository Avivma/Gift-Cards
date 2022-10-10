package com.example.composefirsttry.giftcard.ui.favorites.states

import com.example.composefirsttry.giftcard.model.GiftCard
import com.example.composefirsttry.giftcard.model.Store

sealed class FavoritesIntention {
    data class OpenStoreDialog(val store: Store) : FavoritesIntention()
    object OpenRemoveAllDialog : FavoritesIntention()
    data class RemoveStore(val store: Store) : FavoritesIntention()
    object RemoveAllStores : FavoritesIntention()
    object Refresh : FavoritesIntention()

    sealed class Navigation: FavoritesIntention() {
        data class NavigateToCardsScreen(val card: GiftCard): FavoritesIntention.Navigation()
    }
}