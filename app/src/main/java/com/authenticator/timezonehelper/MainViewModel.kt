package com.authenticator.timezonehelper

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.*
import us.dustinj.timezonemap.TimeZone
import us.dustinj.timezonemap.TimeZoneMap
import java.time.*
import java.util.*

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

    fun getTimezoneCurrentTime(timeZone: String): LocalDateTime {
        val zoneId: ZoneId = ZoneId.of(timeZone)
        return LocalDateTime.now(zoneId)
    }

    fun convertToDestinationTime(timeZoneSource: CharSequence, timeZoneDest: CharSequence, sourceTime: CharSequence): String {
        // TODO: refactor with time formatting helper method
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        // Parse source time string into Date object
        val formattedSourceDateTime = simpleDateFormat.parse(sourceTime.toString())

        // Get Local times at source and destination
        val sourceLocalTime = getTimezoneCurrentTime(timeZoneSource.toString())
        val destLocalTime = getTimezoneCurrentTime(timeZoneDest.toString())

        val timeDiff = Duration.between(sourceLocalTime, destLocalTime)

        // Add time difference to destination time and format for rendering as a string in the UI
        val calendar = Calendar.getInstance()
        calendar.time = formattedSourceDateTime
        calendar.add(Calendar.SECOND, timeDiff.seconds.toInt())
        return calendar.time.toString()
    }
}