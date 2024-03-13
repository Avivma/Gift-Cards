package com.example.composefirsttry.giftcard.ui.main.states

import com.example.composefirsttry.giftcard.logic.cards.model.GiftCard
import com.example.composefirsttry.giftcard.logic.cards.model.GiftCardType
import com.example.composefirsttry.giftcard.logic.stores.model.Store
import com.example.composefirsttry.giftcard.ui.main.cardutils.CardModel

sealed class StoreMainState(
    val progressBarVisible: Boolean = false,
    val storesListFaded: Boolean = false) {

    object Waiting: StoreMainState(progressBarVisible = true, storesListFaded = true)
    data class DisplayData(val cardModel: CardModel, val stores: List<Store>, val hideStoreSelectionFilter: Boolean = false, val searchIconVisible: Boolean = true): StoreMainState()
    data class SearchBoxTextChanged(val searchText: String, val cursorPosition: Int) : StoreMainState()
    data class StoreSelected(val store: Store, val storeSelectionFilterVisible: Boolean): StoreMainState()
    data class StoreDialogOpened(val store: Store) : StoreMainState()
    data class DisplayToast(val cards: List<GiftCard>, val singleCard: Boolean) : StoreMainState()
    object DisplayForceInitializeDialog: StoreMainState()

    sealed class Navigation: StoreMainState() {
        data class NavigateToCardsScreen(val giftCardType: GiftCardType): StoreMainState.Navigation()
        object NavigateToInitializeScreen: StoreMainState.Navigation()
    }
}