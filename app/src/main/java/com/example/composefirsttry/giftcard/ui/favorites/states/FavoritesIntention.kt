package com.example.composefirsttry.giftcard.ui.favorites.states

import com.example.composefirsttry.giftcard.logic.cards.model.GiftCardType
import com.example.composefirsttry.giftcard.logic.stores.model.Store

sealed class FavoritesIntention {
    data class OpenStoreDialog(val store: Store) : FavoritesIntention()
    object OpenRemoveAllDialog : FavoritesIntention()
    data class RemoveStore(val store: Store) : FavoritesIntention()
    object RemoveAllStores : FavoritesIntention()
    object Refresh : FavoritesIntention()
    data class NavigateToCardsScreen(val giftCardType: GiftCardType): FavoritesIntention()
}