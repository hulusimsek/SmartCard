package com.hulusimsek.smartcard

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SmartCardApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}