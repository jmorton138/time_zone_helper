package com.authenticator.timezonehelper

import android.app.Application
import android.content.Context
import androidx.room.Room


class AppContainer(applicationContext: Context) {
    private val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "world-cities.db")
        .createFromAsset("database/simplemaps-world-cities.db")
        .fallbackToDestructiveMigration()
        .build()
    private val cityDao = db.cityDao()
    private val cityRepository = CityRepository(cityDao)
    val mainViewModelFactory = MainViewModelFactory(cityRepository)
}