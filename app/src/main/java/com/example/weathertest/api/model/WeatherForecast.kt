package com.example.weathertest.api.model

import com.google.gson.annotations.SerializedName
import java.util.*

class WeatherForecast {
    @SerializedName("list")
    var forecast: List<Weather>? = null

    fun getWeeklyForecast(): List<Weather>? {
        if (forecast.isNullOrEmpty()) return null
        val temp = ArrayList<Weather>()
        var maxHum = -1.0
        var minHum = 101.0
        var minTemp = 200.0
        var maxTemp = -200.0
        var timeTemp = forecast!![0].time
        var windTemp = forecast!![0].wind
        var weatherDescr = forecast!![0].weatherDescription
        var day = forecast!![0].date.get(Calendar.DAY_OF_MONTH)
        var weatherTemp = forecast!![0]
        for (weather in forecast!!) {
            if (weather.date.get(Calendar.DAY_OF_MONTH) != day) {
                day = weather.date.get(Calendar.DAY_OF_MONTH)
                val weatherToAdd = Weather(weatherTemp).apply {
                    weatherDescription = weatherDescr
                    time = timeTemp
                    wind = windTemp
                    setTemperatureRange(minTemp, maxTemp)
                    humidity = (maxHum + minHum) / 2
                }
                temp.add(weatherToAdd)
                minHum = weather.humidity
                maxHum = weather.humidity
                minTemp = weather.temperature
                maxTemp = weather.temperature
                weatherTemp = weather
            }
            if (weather.date.get(Calendar.HOUR_OF_DAY) == 14) {
                weatherDescr = weather.weatherDescription
                timeTemp = weather.time
                windTemp = weather.wind
            }
            if (minHum > weather.humidity) minHum = weather.humidity
            if (maxHum < weather.humidity) maxHum = weather.humidity
            if (minTemp > weather.temperature) minTemp = weather.temperature
            if (maxTemp < weather.temperature) maxTemp = weather.temperature
        }
        return temp
    }

    fun getDailyForecastByDay(day: Int): List<Weather>? {
        if (forecast.isNullOrEmpty()) return null
        val temp = ArrayList<Weather>()
        for (weather in forecast!!) {
            if (weather.date.get(Calendar.DAY_OF_MONTH) == day)
                temp.add(weather)
            else if (temp.isNotEmpty() && temp.size <= 8)
                temp.add(weather)
        }
        return temp
    }
}