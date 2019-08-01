package com.example.weathertest


import android.Manifest
import android.app.AlertDialog
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weathertest.adapters.DayAdapter
import com.example.weathertest.adapters.WeekAdapter
import com.example.weathertest.api.model.Weather
import com.example.weathertest.api.model.WeatherForecast
import com.example.weathertest.utils.AppPref
import com.example.weathertest.utils.Utils
import com.example.weathertest.utils.WeatherManager
import kotlinx.android.synthetic.main.fragment_main.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

const val RQ_LOCATION = 1

class MainFragment : Fragment(), LocationListener {
    private var weekAdapter: WeekAdapter? = null
    private var dayAdapter: DayAdapter? = null
    private var weatherManager: WeatherManager? = null
    private var weatherForecast: WeatherForecast? = null
    private var locationManager: LocationManager? = null
    private val formatter: DateFormat = SimpleDateFormat("EE, dd MMM")

    private val onDayItemClickListener = object : WeekAdapter.OnItemClickListener {
        override fun onClick(day: Weather) {
            dayAdapter?.updateData(weatherForecast?.getDailyForecastByDay(day.date.get(Calendar.DAY_OF_MONTH))!!)
            recyclerTemperature.scheduleLayoutAnimation()
            textTemperature?.text = day.temperatureRange
            textHumidity?.text = day.humidityStr
            textWind?.text = "${day.windSpeed}м/сек"
            imageWindDir?.rotation = day.windDirection.toFloat()
            if (day.weatherDescription != null)
                imageWeather?.setImageResource(Utils.getWeatherIcon(day.weatherDescription!!, day.date.get(Calendar.HOUR_OF_DAY)))
        }
    }

    private val onWeatherLoadedListener = object : WeatherManager.OnWeatherLoadedListener {
        override fun onSuccess(weatherForecast: WeatherForecast) {
            locationManager?.removeUpdates(this@MainFragment)
            if (weatherForecast.forecast == null) return

            this@MainFragment.weatherForecast = weatherForecast
            val weeklyForecast = weatherForecast.getWeeklyForecast()!!
            weekAdapter?.updateData(weeklyForecast)
            val day = weatherForecast.forecast!![0].date.get(Calendar.DAY_OF_MONTH)
            dayAdapter?.updateData(weatherForecast.getDailyForecastByDay(day)!!)
            recyclerTemperature.scheduleLayoutAnimation()

            textTemperature?.text = weeklyForecast[0].temperatureRange
            textHumidity?.text = weeklyForecast[0].humidityStr
            textDayOfWeek?.text = formatter.format(weatherForecast.forecast!![0].date.time)
            textWind?.text = "${weeklyForecast[0].windSpeed}м/сек"
            imageWindDir?.rotation = weeklyForecast[0].windDirection.toFloat()

            if (weeklyForecast[0].weatherDescription != null)
                imageWeather?.setImageResource(
                        Utils.getWeatherIcon(weeklyForecast[0].weatherDescription!!,
                                weeklyForecast[0].date.get(Calendar.HOUR_OF_DAY)))
        }

        override fun onFailure(error: String) {
            Toast.makeText(context!!, error, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        locationManager = context!!.getSystemService(LOCATION_SERVICE) as LocationManager?
        initManager()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weekAdapter = WeekAdapter(context!!, onDayItemClickListener)
        dayAdapter = DayAdapter(context!!)
        recyclerDates?.adapter = weekAdapter
        recyclerDates?.layoutManager = LinearLayoutManager(context)

        recyclerTemperature?.adapter = dayAdapter
        recyclerTemperature?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val animation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_anim_appear)
        recyclerTemperature.layoutAnimation = animation
    }

    override fun onLocationChanged(location: Location?) {
        weatherManager?.lat = location!!.latitude
        weatherManager?.lon = location.longitude
        weatherManager?.getWeather(onWeatherLoadedListener)
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
    }

    override fun onProviderEnabled(p0: String?) {
    }

    override fun onProviderDisabled(p0: String?) {
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            RQ_LOCATION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    getCurrentLocationWeather()
            }
        }
    }

    fun updateWeather(latitude: Double, longitude: Double) {
        weatherManager?.lat = latitude
        weatherManager?.lon = longitude
        weatherManager?.getWeather(onWeatherLoadedListener)
    }

    fun getCurrentLocationWeather() {
        if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), RQ_LOCATION)
        } else {
            if (!locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)!!) {
                val builder = AlertDialog.Builder(context!!)
                builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                        .setCancelable(false)
                        .setPositiveButton("Yes") { dialog, id -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
                        .setNegativeButton("No") { dialog, id -> dialog.cancel() }
                val alert = builder.create()
                alert.show()
            } else {
                locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 2000f, this)
            }
        }
    }

    private fun initManager() {
        val coords = AppPref.getCoords(context!!)
        weatherManager = WeatherManager()
        if (coords != null) {
            weatherManager?.lat = coords.latitude
            weatherManager?.lon = coords.longitude
            weatherManager?.getWeather(onWeatherLoadedListener)
        } else {
            getCurrentLocationWeather()
        }
    }
}
