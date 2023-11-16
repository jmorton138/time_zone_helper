package com.authenticator.timezonehelper

import androidx.room.Dao
import androidx.room.Query


@Dao
interface CityDao {
    @Query("SELECT * FROM city")
    fun getAllCities(): List<City>

    @Query("SELECT city_ascii FROM city")
    fun getAllCityNames(): List<String>

    @Query("SELECT * FROM city WHERE city_ascii = :name")
    fun findCityByName(name: String): City

    @Query("SELECT lat FROM city WHERE city_ascii = :name")
    fun getLatitude(name: String): Double

    @Query("SELECT lng FROM city WHERE city_ascii = :name")
    fun getLongitude(name: String): Double


}
