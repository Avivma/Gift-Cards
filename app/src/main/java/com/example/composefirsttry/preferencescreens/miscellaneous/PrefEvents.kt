package com.example.composefirsttry.preferencescreens.miscellaneous

import com.example.composefirsttry.preferencescreens.fragments.ParentPrefItem

sealed class PrefEvents() {
    data class LandingScreen(val prefList: ArrayList<ParentPrefItem>) : PrefEvents()
    data class StateChanged(val prefItem: ParentPrefItem) : PrefEvents()
    data class Customize(val prefItem: ParentPrefItem) : PrefEvents()
}
