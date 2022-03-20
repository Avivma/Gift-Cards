package com.example.composefirsttry

import android.app.Application
import com.example.composefirsttry.di.AppComponent
import com.example.composefirsttry.di.AppModule
import com.example.composefirsttry.di.DaggerAppComponent

class MyApplication: Application() {
    lateinit var component: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        setup()
    }

    private fun setup() {
        L.setup()
        component = DaggerAppComponent.builder()
            .appModule(AppModule(applicationContext))
            .build()
    }
}