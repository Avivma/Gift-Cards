package com.example.composefirsttry.di

import com.example.composefirsttry.giftcard.GiftCardMainActivity
import com.example.composefirsttry.giftcard.ui.GiftCardStoresMainFragment
import com.example.composefirsttry.giftcard.viewmodel.StoresMainViewModel
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