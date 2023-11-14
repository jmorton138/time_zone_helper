package com.authenticator.timezonehelper

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.*
import us.dustinj.timezonemap.TimeZone
import us.dustinj.timezonemap.TimeZoneMap
import java.time.*
import java.util.*

class MainViewModel(private val cityRepository: CityRepository, private val timeZoneMap: TimeZoneMap): ViewModel() {
    private val monthsList = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
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

    private fun getTimezoneCurrentTime(timeZone: String): LocalDateTime {
        val zoneId: ZoneId = ZoneId.of(timeZone)
        return LocalDateTime.now(zoneId)
    }

    fun convertToDestinationTime(
        timeZoneSource: CharSequence,
        timeZoneDest: CharSequence,
        sourceTime: CharSequence
    ): String {
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
        val splitCalendarDate = calendar.time.toString().split(" ")
        val year = splitCalendarDate[5]
        val month = monthsList.indexOf(splitCalendarDate[1]) + 1
        val day = splitCalendarDate[2]
        val time = splitCalendarDate[3]

//        Alternative: Going with the other approach for now because even though indexes are
//        hardcoded, this formatting is unlikely to change and this actually requires less
//        string parsing because I don't need to worry about formatting single digits. The extra
//        memory for the small months list seems worth this trade off.
//        val year = calendar.time.year
//        val month = calendar.time.month + 1
//        val day = calendar.time.day
//        val time = calendar.time.hours + calendar.time.minutes + calendar.time.seconds

        return "$year-$month-$day $time"
    }
}