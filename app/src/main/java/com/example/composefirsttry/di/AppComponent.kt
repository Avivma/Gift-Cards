package com.example.composefirsttry.di

import com.example.composefirsttry.giftcard.GiftCardMainActivity
import com.example.composefirsttry.giftcard.ui.main.GiftCardStoresMainFragment
import com.example.composefirsttry.giftcard.ui.main.StoresMainViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(giftCardStoresMainFragment: GiftCardStoresMainFragment)
    fun inject(giftCardStoresMainFragment: StoresMainViewModel)
    fun inject(giftCardMainActivity: GiftCardMainActivity) {
    }
}