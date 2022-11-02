package com.example.composefirsttry.giftcard.ui.utils

import com.example.composefirsttry.L
import com.example.composefirsttry.giftcard.logic.stores.model.Store

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
        const val INVISIBLE = 0
        const val VISIBLE = 1
        const val ACTIVE = 2
    }
}