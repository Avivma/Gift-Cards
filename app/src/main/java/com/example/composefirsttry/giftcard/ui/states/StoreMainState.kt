package com.example.composefirsttry.giftcard.ui.states

import com.example.composefirsttry.giftcard.model.Store

sealed class StoreMainState(
    val progressBarVisible: Boolean,
    val storesListFaded: Boolean) {

    object Waiting: StoreMainState(progressBarVisible = true, storesListFaded = true)
    data class DisplayData(val stores: List<Store>): StoreMainState(progressBarVisible = false, storesListFaded = false)
}