package com.authenticator.timezonehelper

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface CityDao {
    @Query("SELECT * FROM city")
    fun getAllCities(): List<City>

    @Query("SELECT * FROM city WHERE city_ascii = :name")
    fun findCityByName(name: String): City


}
