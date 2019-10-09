package com.example.weathertest.api

import androidx.annotation.MainThread
import com.example.weathertest.WeatherManager
import com.example.weathertest.api.model.WeatherForecast
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


const val BASE_URL = "http://api.openweathermap.org/data/2.5/"
const val API_KEY = "a48756106068eca02fcdb16115b95816"
const val UNITS = "metric"

class WeatherManagerImpl : WeatherManager {


    private var api: WeatherApi? = null
    var lat = 47.0303
    var lon = 35.0234

    init {
        api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }

    override fun updateLocation(latitude: Double, longitude: Double) {
        lat = latitude
        lon = longitude
    }

    override fun getWeather(): Single<WeatherForecast>? {
        return api?.getWeather(lat, lon, UNITS, API_KEY)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
    }
}