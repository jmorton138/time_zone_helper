package com.authenticator.timezonehelper

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city")
data class City(
    @PrimaryKey @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "city_name") val cityName: String?,
    @ColumnInfo(name = "city_ascii") val cityAscii: String?,
    @ColumnInfo(name = "lat") val latitude: Double?,
    @ColumnInfo(name = "lng") val longitude: Double?,
    @ColumnInfo(name = "country") val country: String?,
    @ColumnInfo(name = "iso2") val iso2: String?,
    @ColumnInfo(name = "iso3") val iso3: String?,
    @ColumnInfo(name = "admin_name") val adminName: String?,
    @ColumnInfo(name = "capital") val capital: String?,
    @ColumnInfo(name = "population") val population: Double?,
)
