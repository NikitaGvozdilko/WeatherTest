package com.example.weathertest

import android.app.Application
import com.google.android.libraries.places.api.Places

class WeatherApp: Application() {
    override fun onCreate() {
        super.onCreate()
        Places.initialize(applicationContext, "AIzaSyCK-dka514Uul6r5w6WILPwHluaohRJ-Eo")
        Places.createClient(this)
    }
}