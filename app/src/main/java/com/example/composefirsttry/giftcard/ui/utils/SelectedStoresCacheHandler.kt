package com.example.composefirsttry.giftcard.ui.utils

import com.example.composefirsttry.L
import com.example.composefirsttry.giftcard.logic.stores.model.Store
import com.example.composefirsttry.giftcard.ui.main.states.StoreMainState

class SelectedStoresCacheHandler {
    private val selectedStoredCache: MutableSet<Store> = mutableSetOf()
    var storesHasBeenSelected: Int = INVISIBLE
        private set

    fun updateStoreWithCacheProperties(store: Store) {
        store.selected = selectedStoredCache.contains(store)
    }

    fun updateCache(store: Store) {
        if (store.selected) {
            selectedStoredCache.add(store)
            if (storesHasBeenSelected == INVISIBLE) storesHasBeenSelected = VISIBLE
        } else {
            selectedStoredCache.remove(store)
            if (selectedStoredCache.none { it.selected }) storesHasBeenSelected = INVISIBLE
        }
    }

    fun hasStoreIncludedInCache(store: Store): Boolean = store.selected || storesHasBeenSelected != ACTIVE //equivalent to: storesHasBeenSelected == ACTIVE -> store.selected

    fun clearStoresSelection() {
        if (storesHasBeenSelected == ACTIVE) { //deactivate
            storesHasBeenSelected = VISIBLE
        } else { //make invisible
            selectedStoredCache.clear()
            storesHasBeenSelected = INVISIBLE
        }
    }

    fun setStoresSelection(selectionState: Int) {
        L.i("setStoreSelection: selectionState= $selectionState")
        storesHasBeenSelected = selectionState
    }

    companion object {
        private const val INVISIBLE = StoreMainState.StoreSelection.INVISIBLE
        private const val VISIBLE = StoreMainState.StoreSelection.VISIBLE
        private const val ACTIVE = StoreMainState.StoreSelection.ACTIVE
    }
}