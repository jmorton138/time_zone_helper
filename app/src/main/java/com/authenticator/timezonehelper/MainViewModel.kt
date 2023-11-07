package com.authenticator.timezonehelper

import androidx.lifecycle.*

class MainViewModel(private val cityRepository: CityRepository): ViewModel() {
    suspend fun getCityNames(): List<String> = cityRepository.getAllCityNames()
}