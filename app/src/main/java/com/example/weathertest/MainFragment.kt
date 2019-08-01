package com.example.weathertest


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weathertest.adapters.DayAdapter
import com.example.weathertest.adapters.WeekAdapter
import com.example.weathertest.api.model.Weather
import com.example.weathertest.api.model.WeatherForecast
import com.example.weathertest.utils.AppPref
import com.example.weathertest.utils.Utils
import kotlinx.android.synthetic.main.fragment_main.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import android.view.animation.AnimationUtils
import com.example.weathertest.utils.WeatherManager


class MainFragment : Fragment() {
    private var weekAdapter: WeekAdapter? = null
    private var dayAdapter: DayAdapter? = null
    private var weatherManager: WeatherManager? = null
    private var weatherForecast: WeatherForecast? = null
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
            if (weatherForecast.forecast == null) return

            this@MainFragment.weatherForecast = weatherForecast
            val weeklyForecast = weatherForecast.getWeeklyForecast()!!
            weekAdapter?.updateData(weeklyForecast)
            val day = weatherForecast.forecast!![0].date.get(Calendar.DAY_OF_MONTH)
            dayAdapter?.updateData(weatherForecast.getDailyForecastByDay(day)!!)

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
        val coords = AppPref.getCoords(context!!)
        weatherManager = WeatherManager()
        if (coords != null) {
            weatherManager?.lat = coords.latitude
            weatherManager?.lon = coords.longitude
        }
        weatherManager?.getWeather(onWeatherLoadedListener)
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

    fun updateWeather(latitude: Double, longitude: Double) {
        weatherManager?.lat = latitude
        weatherManager?.lon = longitude
        weatherManager?.getWeather(onWeatherLoadedListener)
    }
}
