package com.example.composefirsttry.preferencescreens

import com.example.composefirsttry.preferencescreens.fragments.MainPrefItem

sealed class PrefEvents() {
    data class LandingScreen(val prefList: ArrayList<MainPrefItem>) : PrefEvents()
    data class StateChanged(val prefItem: MainPrefItem) : PrefEvents()
    data class Customize(val prefItem: MainPrefItem) : PrefEvents()
}
