package com.example.weathertest.fragments.mainFragment

import com.example.weathertest.api.model.Weather

interface IMainView {
    fun updateWeatherInfo(weather: Weather)
}