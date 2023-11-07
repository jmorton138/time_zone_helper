package com.authenticator.timezonehelper

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CityRepository (private val cityDao : CityDao) {
    suspend fun getAllCityNames(): List<String> {
        return withContext(Dispatchers.IO) {
            val cityNames  = cityDao.getAllCityNames()
            cityNames
        }
    }
}
