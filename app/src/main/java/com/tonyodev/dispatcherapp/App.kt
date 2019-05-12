package com.tonyodev.dispatcherapp

import android.app.Application
import com.tonyodev.dispatchandroid.AndroidDispatcher

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidDispatcher.init()
    }

}