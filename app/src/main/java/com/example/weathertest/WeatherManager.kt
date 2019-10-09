package com.example.weathertest

import com.example.weathertest.api.model.WeatherForecast
import io.reactivex.Single

interface WeatherManager {
    fun getWeather(): Single<WeatherForecast>?
    fun updateLocation(latitude: Double, longitude: Double)
}