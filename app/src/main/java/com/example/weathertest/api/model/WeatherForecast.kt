package com.example.weathertest.api.model

import com.google.gson.annotations.SerializedName
import java.util.*

class WeatherForecast {
    @SerializedName("list")
    var forecast: List<Weather>? = null

    //Возвращает прогноз на 15:00 каждого дня.
    //Если текущий день прошел отметку 15:00, то на этот день возвращает самый ранний прогноз
    fun getWeeklyForecast(): List<Weather>? {
        if (forecast.isNullOrEmpty()) return null
        val temp = ArrayList<Weather>()
        /*int today_min_h = forecast.get(0).getDate().get(Calendar.HOUR_OF_DAY);
        if (today_min_h > 15) {
            temp.add(forecast.get(0));
        }*/
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
                minTemp = 200.0
                maxTemp = -200.0
                weatherTemp = weather
            }
            if (weather.date.get(Calendar.HOUR_OF_DAY) == 15) {
                weatherDescr = weather.weatherDescription
                timeTemp = weather.time
                windTemp = weather.wind
            }
            if (minHum > weather.humidity) minHum = weather.humidity
            if (maxHum < weather.humidity) maxHum = weather.humidity
            if (minTemp > weather.temperature) minTemp = weather.temperature
            if (maxTemp < weather.temperature) maxTemp = weather.temperature
        }
        val weatherToAdd = Weather(weatherTemp)
        weatherToAdd.setTemperatureRange(minTemp, maxTemp)
        return temp
    }

    //Возвращает подробный прогноз на остаток дня
    //shift - сдвиг ( 0 - текущий день, 1 - следующий, 2 - через день.. )
    fun getDailyForecast(shift: Int): List<Weather>? {
        if (forecast.isNullOrEmpty()) return null
        val temp = ArrayList<Weather>()
        val day_num = forecast!![shift * 8].date.get(Calendar.DAY_OF_MONTH)
        for (weather in forecast!!) {
            if (weather.date.get(Calendar.DAY_OF_MONTH) == day_num)
                temp.add(weather)
            else if (!temp.isEmpty() && temp.size < 10)
                temp.add(weather)
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


    /*fun getDailyForecast(shift: Int): List<Weather>? {
        if (forecast.isNullOrEmpty()) return null
        val temp = ArrayList<Weather>()
        val day_num = forecast!![shift * 8].date.get(Calendar.DAY_OF_MONTH)
        for (weather in forecast!!) {
            if (weather.date.get(Calendar.DAY_OF_MONTH) == day_num)
                temp.add(weather)
            else if (!temp.isEmpty() && temp.size < 10)
                temp.add(weather)


        }
        return temp
    }*/
}