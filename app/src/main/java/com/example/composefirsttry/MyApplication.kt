package com.example.composefirsttry

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        setup()
    }

    private fun setup() {
        L.setup()
    }
}