package com.example.composefirsttry.giftcard.ui.main.states

import com.example.composefirsttry.giftcard.logic.stores.model.Store
import com.example.composefirsttry.giftcard.ui.common.dialog.common.CustomDialogAdapterItem
import com.example.composefirsttry.giftcard.ui.common.model.ClubIdAndUrl
import com.example.composefirsttry.giftcard.ui.main.StoreMainTextModel

sealed class StoreMainState(
    val progressBarVisible: Boolean = false,
    val storesListFaded: Boolean = false) {

    object Waiting: StoreMainState(progressBarVisible = true, storesListFaded = true)
    data class DisplayData(val storesAndClubs: Map<Store, List<ClubIdAndUrl>>, val cardsTextModel: StoreMainTextModel, val hideStoreSelectionFilter: Boolean = false, val searchIconVisible: Boolean = true): StoreMainState()
    data class SearchBoxTextChanged(val searchText: String, val cursorPosition: Int) : StoreMainState()
    data class StoreSelected(val store: Store, val storeSelectionFilterVisible: Boolean): StoreMainState()
    data class StoreDialogOpened(val store: Store) : StoreMainState()
    object DisplayForceInitializeDialog: StoreMainState()
    data class CardsDialogOpened(val dialogAdapterItems: List<CustomDialogAdapterItem.Selectable>) : StoreMainState()

    sealed class Navigation: StoreMainState() {
        data class NavigateToCardsScreen(val shoppingClubId: String): StoreMainState.Navigation()
        object NavigateToInitializeScreen: StoreMainState.Navigation()
    }
}