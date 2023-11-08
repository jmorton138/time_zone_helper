package com.authenticator.timezonehelper

import android.app.Application

class TimeZoneHelperApplication : Application() {
    lateinit var appContainer: AppContainer
    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(applicationContext)

    }
}