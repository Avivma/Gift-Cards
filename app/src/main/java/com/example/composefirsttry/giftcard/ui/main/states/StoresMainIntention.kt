package com.example.composefirsttry.giftcard.ui.main.states

import com.example.composefirsttry.giftcard.logic.stores.model.Store
import com.example.composefirsttry.giftcard.ui.common.dialog.common.CustomDialogAdapterItem

sealed class StoresMainIntention {
    //Filters
    data class FilterByPrefix(val prefix: String): StoresMainIntention()
    object FilterBySelectedStores: StoresMainIntention()
    object ClearSearchBox : StoresMainIntention()
    object AddSeparationMarkToSearch : StoresMainIntention()
    object OpenCardsSelectionDialog : StoresMainIntention()
    //Store Selection
    data class SelectStore(val store: Store): StoresMainIntention()
    object ClearStoresSelection: StoresMainIntention()
    //Favorites
    data class OpenStoreDialog(val store: Store) : StoresMainIntention()
    data class AddStoreToFavorites(val store: Store) : StoresMainIntention()
    //Navigation
    data class NavigateToCardsScreen(val shoppingClubId: String): StoresMainIntention()

    class ShoppingClubChecked(val clubDialogAdapterItem: CustomDialogAdapterItem.Selectable) : StoresMainIntention()

    object Refresh: StoresMainIntention()
    object Initialize : StoresMainIntention()
}
