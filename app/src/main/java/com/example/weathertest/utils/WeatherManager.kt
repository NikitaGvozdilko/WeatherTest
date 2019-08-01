package com.example.weathertest.utils

import com.example.weathertest.api.WeatherApi
import com.example.weathertest.api.model.WeatherForecast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


const val BASE_URL = "http://api.openweathermap.org/data/2.5/"
const val API_KEY = "a48756106068eca02fcdb16115b95816"
const val UNITS = "metric"
class WeatherManager {
    private var api: WeatherApi? = null
    var lat = 47.0303
    var lon = 35.0234

    init {
        api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)

    }

    fun getWeather(onWeatherLoadedListener: OnWeatherLoadedListener) {
        api?.getWeather(lat, lon, UNITS, API_KEY)?.enqueue(object : Callback<WeatherForecast> {
            override fun onResponse(call: Call<WeatherForecast>, response: Response<WeatherForecast>) {
                if(response.body() != null)
                    onWeatherLoadedListener.onSuccess(response.body()!!)
                else onWeatherLoadedListener.onFailure("No data")
            }

            override fun onFailure(call: Call<WeatherForecast>, t: Throwable) {
                onWeatherLoadedListener.onFailure(t.message!!)
            }
        })
    }

    interface OnWeatherLoadedListener {
        fun onSuccess(weatherForecast: WeatherForecast)
        fun onFailure(error: String)
    }
}