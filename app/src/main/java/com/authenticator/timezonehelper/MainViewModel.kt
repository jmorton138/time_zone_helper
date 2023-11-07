package com.authenticator.timezonehelper

import androidx.lifecycle.*
import us.dustinj.timezonemap.TimeZone
import us.dustinj.timezonemap.TimeZoneMap
import java.time.*

class MainViewModel(private val cityRepository: CityRepository, private val timeZoneMap: TimeZoneMap): ViewModel() {
    suspend fun getCityNames(): List<String> = cityRepository.getAllCityNames()

    suspend fun getLatitude(cityName: String): Double {
        return cityRepository.getLatitude(cityName)
    }
    suspend fun getLongitude(cityName: String): Double {
        return cityRepository.getLongitude(cityName)
    }
    fun getTimeZoneFromCoordinates(lat: Double, lng: Double): String {
        val timeZone: TimeZone? = timeZoneMap.getOverlappingTimeZone(lat, lng)
        return timeZone?.zoneId ?: "Not Found"
    }

    fun getTimezoneCurrentTime(timeZone: String) : Long {
        val zoneId: ZoneId = ZoneId.of("America/New_York")
        val currentZonedDateTime: ZonedDateTime = ZonedDateTime.now(zoneId)
        val currentTimestamp: Long = currentZonedDateTime.toInstant().toEpochMilli()
        return currentTimestamp
    }
}