package com.example.composefirsttry.giftcard.ui.main.states

import com.example.composefirsttry.giftcard.logic.stores.model.Store

sealed class StoreMainState(
    val progressBarVisible: Boolean = false,
    val storesListFaded: Boolean = false) {

    object Waiting: StoreMainState(progressBarVisible = true, storesListFaded = true)
    data class DisplayData(val stores: List<Store>, val hideStoreSelectionFilter: Boolean = false): StoreMainState()
    data class StoreSelected(val store: Store, val storeSelectionFilterVisible: Boolean): StoreMainState()
    data class StoreDialogOpened(val store: Store) : StoreMainState()
}