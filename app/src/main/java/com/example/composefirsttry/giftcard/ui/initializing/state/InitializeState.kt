package com.example.composefirsttry.giftcard.ui.initializing.state

sealed class InitializeState {
    sealed class Navigation: InitializeState() {
        object NavigateToMainScreen: InitializeState.Navigation()
        object NavigateToLandingScreen: InitializeState.Navigation()
    }
}

