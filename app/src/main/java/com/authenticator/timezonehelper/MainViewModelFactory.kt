package com.authenticator.timezonehelper

import us.dustinj.timezonemap.TimeZoneMap

class MainViewModelFactory(private val cityRepository: CityRepository, private val timeZoneMap: TimeZoneMap): Factory<MainViewModel> {
    override fun create(): MainViewModel {
        return MainViewModel(cityRepository, timeZoneMap)
    }
}