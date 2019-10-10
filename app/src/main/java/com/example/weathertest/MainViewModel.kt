package com.example.weathertest

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weathertest.api.WeatherManagerImpl
import com.example.weathertest.api.model.Weather
import com.example.weathertest.api.model.WeatherForecast
import com.example.weathertest.fragments.mainFragment.RQ_LOCATION

class MainViewModel(private val weatherManager: WeatherManager,
                    private val mLocationManager: LocationManager) : ViewModel() {
    val weather = MutableLiveData<WeatherForecast>()

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location?) {
            updateLocation(location!!.latitude, location.longitude)
            getWeather()
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        }

        override fun onProviderEnabled(p0: String?) {
        }

        override fun onProviderDisabled(p0: String?) {
        }
    }

    fun getWeather() {
        weatherManager?.getWeather()?.subscribe({
            if (it?.forecast == null) return@subscribe
            weather?.value = it
            mLocationManager?.removeUpdates(locationListener)
        }, {
            println("ERROR" + it.message)
        })
    }

    fun getWeatherFromLD() : WeatherForecast? {
        return weather.value
    }

    fun getDailyForecastByDay(day: Int): List<Weather>? {
        return weather.value?.getDailyForecastByDay(day)
    }

    fun getWeeklyForecast(): List<Weather>? {
        return weather.value?.getWeeklyForecast()
    }

    fun updateLocation(latitude: Double, longitude: Double) {
        weatherManager.updateLocation(latitude, longitude)
    }

    fun getCurrentLocationWeather(activity: FragmentActivity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                RQ_LOCATION
            )
        } else {
            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                val builder = AlertDialog.Builder(activity)
                builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { dialog, id -> activity.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
                    .setNegativeButton("No") { dialog, id -> dialog.cancel() }
                val alert = builder.create()
                alert.show()
            } else {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 2000f, locationListener)
            }
        }
    }

    fun containData() : Boolean {
        return weather.value != null
    }

    class CustomViewModelFactory(private val weatherManager: WeatherManager,
                                 private val mLocationManager: LocationManager) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return  MainViewModel(weatherManager, mLocationManager) as T
        }
    }
}

