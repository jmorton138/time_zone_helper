package com.authenticator.timezonehelper

import android.app.Application
import android.util.Log
import androidx.room.Room

class TimeZoneHelperApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // initialize DB
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "world-cities.db")
            .createFromAsset("database/simplemaps-world-cities.db")
            .fallbackToDestructiveMigration()
            .build()
        Log.e("db created", "hi")
    }
}