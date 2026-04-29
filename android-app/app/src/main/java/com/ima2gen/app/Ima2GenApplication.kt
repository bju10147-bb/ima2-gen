package com.ima2gen.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Ima2GenApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: Ima2GenApplication
            private set
    }
}
