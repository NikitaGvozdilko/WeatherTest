package com.example.weathertest

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import com.example.weathertest.adapters.DayAdapter
import com.example.weathertest.adapters.WeekAdapter
import com.example.weathertest.api.model.Weather
import com.example.weathertest.api.model.WeatherForecast
import com.example.weathertest.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private var weekAdapter: WeekAdapter? = null
    private var dayAdapter: DayAdapter? = null
    private var weatherManager: WeatherManager? = null
    private var weatherForecast: WeatherForecast? = null
    private val formatter: DateFormat = SimpleDateFormat("EE, dd MMM")
    private val onDayItemClickListener = object : WeekAdapter.OnItemClickListener {
        override fun onClick(day: Weather) {
            dayAdapter?.updateData(weatherForecast?.getDailyForecastByDay(day.date.get(Calendar.DAY_OF_MONTH))!!)
            textTemperature.text = day.temperatureRange
            textHumidity.text = day.humidityStr
            textWind.text = "${day.windSpeed}м/сек"
            imageWindDir.rotation = day.windDirection.toFloat()
            if (day.weatherDescription != null)
                imageWeather.setImageResource(Utils.getWeatherIcon(day.weatherDescription!!, day.date.get(Calendar.HOUR_OF_DAY)))
        }

    }

    private val onWeatherLoadedListener = object : WeatherManager.OnWeatherLoadedListener {
        override fun onSuccess(weatherForecast: WeatherForecast) {
            if (weatherForecast.forecast == null) return
            this@MainActivity.weatherForecast = weatherForecast
            val weeklyForecast = weatherForecast.getWeeklyForecast()!!
            weekAdapter?.updateData(weeklyForecast)
            dayAdapter?.updateData(weatherForecast.getDailyForecast(0)!!)

            textTemperature.text = weeklyForecast[0].temperatureRange
            textHumidity.text = weeklyForecast[0].humidityStr
            textDayOfWeek.text = formatter.format(weatherForecast.forecast!![0].date.time)
            textWind.text = "${weeklyForecast[0].windSpeed}м/сек"
            imageWindDir.rotation = weeklyForecast[0].windDirection.toFloat()
            if (weeklyForecast[0].weatherDescription != null)
                imageWeather.setImageResource(
                        Utils.getWeatherIcon(weeklyForecast[0].weatherDescription!!,
                                weeklyForecast[0].date.get(Calendar.HOUR_OF_DAY)))

        }

        override fun onFailure(error: String) {
            Toast.makeText(this@MainActivity, error, Toast.LENGTH_LONG).show()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        weekAdapter = WeekAdapter(this, onDayItemClickListener)
        recyclerDates.adapter = weekAdapter
        recyclerDates.layoutManager = LinearLayoutManager(this)
        dayAdapter = DayAdapter(this)
        recyclerTemperature.adapter = dayAdapter
        recyclerTemperature.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        toolbar.title = ""
        setSupportActionBar(toolbar)
        weatherManager = WeatherManager()
        weatherManager?.getWeather(onWeatherLoadedListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_my_location) {

        }

        return true
    }
}
