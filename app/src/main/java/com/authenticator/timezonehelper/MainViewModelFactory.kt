package com.authenticator.timezonehelper

class MainViewModelFactory(private val cityRepository: CityRepository): Factory<MainViewModel> {
    override fun create(): MainViewModel {
        return MainViewModel(cityRepository)
    }
}