package com.example.weathertest.api.model

import com.google.gson.annotations.SerializedName
import java.util.*

class Weather {
    @SerializedName("main")
    private var weatherTemp: WeatherValue? = null

    @SerializedName("weather")
    private var weatherDescr: List<WeatherDescr>? = null

    var wind: Wind? = null

    @SerializedName("dt")
    var time: Long = 0

    var temperatureRange: String? = null

    val date: Calendar
        get() {
            val date = Calendar.getInstance()
            date.timeInMillis = time * 1000
            return date
        }

    val temperatureStr: String
        get() = weatherTemp!!.temp.toInt().toString() + "\u00B0"

    val humidityStr: String
        get() = weatherTemp!!.humidity.toString() + "%"

    val temperature: Double
        get() = weatherTemp!!.temp

    val windSpeed: Double
        get() = wind?.speed ?: 0.0

    val windDirection: Double
        get() = wind?.deg ?: 0.0

    var humidity: Double
        get() = weatherTemp!!.humidity
        set(value) {
            weatherTemp!!.humidity = value
        }

    var weatherDescription: String?
        get() = weatherDescr!![0].main
        set(value) {
            weatherDescr!![0].main = value!!
        }

    constructor(weather: Weather) {
        this.weatherTemp = weather.weatherTemp
        this.weatherDescr = weather.weatherDescr
        this.time = weather.time
        this.wind = weather.wind
    }


    fun setTemperatureRange(minTemp: Double, maxTemp: Double) {
        temperatureRange = "${maxTemp.toInt()}°/${minTemp.toInt()}°"
    }

    data class Wind(
            var speed: Double,
            var deg: Double
    )

    private data class WeatherValue(
            var temp: Double,
            var humidity: Double
    )

    private data class WeatherDescr(
            var main: String
    )
}
