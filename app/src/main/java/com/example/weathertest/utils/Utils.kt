package com.example.weathertest.utils

import com.example.weathertest.R

class Utils {
    companion object {
        fun getWeatherIcon(description: String, time: Int) : Int {
            return when (description) {
                "Clouds" -> if (isDay(time)) R.drawable.ic_white_day_cloudy else R.drawable.ic_white_night_cloudy
                "Clear" -> if (isDay(time)) R.drawable.ic_white_day_bright else R.drawable.ic_white_night_bright
                "Rain" -> if (isDay(time)) R.drawable.ic_white_day_rain else R.drawable.ic_white_night_rain
                "Thunderstorm" -> if (isDay(time)) R.drawable.ic_white_day_thunder else R.drawable.ic_white_night_thunder
                else -> if (isDay(time)) R.drawable.ic_white_day_shower else R.drawable.ic_white_night_shower
            }
        }

        fun isDay(time: Int) = time > 5 && time < 17
    }


}