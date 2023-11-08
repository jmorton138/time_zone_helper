package com.authenticator.timezonehelper

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
//class ExampleUnitTest {
//    @Test
//    fun addition_isCorrect() {
//        assertEquals(4, 2 + 2)
//    }
//}

@RunWith(AndroidJUnit4::class)
class SimpleEntityReadWriteTest {
    private lateinit var cityDao: CityDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).build()
        val db_instance = AppDatabase.getDatabase(context)
        cityDao = db_instance.cityDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun readCityInList() {
        val allCities = cityDao.getAllCities()
        assertEquals(allCities.size, 178764)
        val cityQuery = cityDao.findCityByName("Guangzhou")
        assertEquals(cityQuery.cityName, "Guangzhou")
    }
}