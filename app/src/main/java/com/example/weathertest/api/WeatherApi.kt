package com.example.weathertest.api

import com.example.weathertest.api.model.WeatherForecast
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("forecast")
    fun getWeather(@Query("lat") lat: Int,
                   @Query("lon")lon: Int,
                   @Query("units") units: String,
                   @Query("appid") apiKey: String) : Call<WeatherForecast>
}