package com.example.weathertest.fragments.mainFragment


import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.weathertest.MainViewModel
import com.example.weathertest.R
import com.example.weathertest.adapters.DayAdapter
import com.example.weathertest.adapters.WeekAdapter
import com.example.weathertest.api.model.Weather
import com.example.weathertest.api.model.WeatherForecast
import com.example.weathertest.fragments.mainFragment.di.DaggerMainComponent
import com.example.weathertest.fragments.mainFragment.di.MainModule
import com.example.weathertest.utils.AppPref
import com.example.weathertest.utils.Utils
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.layout_today_info.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


const val RQ_LOCATION = 1

class MainFragment : Fragment(), IMainView {
    private lateinit var mWeekAdapter: WeekAdapter
    private lateinit var mDayAdapter: DayAdapter
    private lateinit var mViewModel: MainViewModel
    private lateinit var mGlide: RequestManager
    private val formatter: DateFormat = SimpleDateFormat("EE, dd MMM")
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val mainComponent = DaggerMainComponent.builder().mainModule(MainModule(activity!!, this)).build()
        mViewModel = mainComponent.getViewModel()
        mDayAdapter = mainComponent.getDayAdapter()
        mWeekAdapter = mainComponent.getWeekAdapter()

        subscribeData()

        mGlide = mainComponent.getGlide()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        resetViews()

        recyclerDates?.adapter = mWeekAdapter
        recyclerDates?.layoutManager = LinearLayoutManager(context)

        recyclerTemperature?.adapter = mDayAdapter
        recyclerTemperature?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val animation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_anim_appear)
        recyclerTemperature.layoutAnimation = animation

        if (!mViewModel.containData())
            loadWeather()
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    override fun updateWeatherInfo(weather: Weather) {
        textDayOfWeek?.text = formatter.format(weather.date.time)
        textTemperature?.text = weather.temperatureRange
        textHumidity?.text = weather.humidityStr
        textWind?.text = "${weather.windSpeed}м/сек"
        imageWindDir?.rotation = weather.windDirection.toFloat()

        val simpleOptions = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        if (weather.weatherDescription != null)
            mGlide.load(
                Utils.getWeatherIcon(
                    weather.weatherDescription!!,
                    weather.date.get(Calendar.HOUR_OF_DAY)
                )
            )
                .apply(simpleOptions)
                .into(imageWeather)

        mDayAdapter.updateData(mViewModel.getDailyForecastByDay(weather.date.get(Calendar.DAY_OF_MONTH))!!)
        recyclerTemperature.scheduleLayoutAnimation()
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
        mViewModel.updateLocation(latitude, longitude)
        mViewModel.getWeather()
    }

    fun getCurrentLocationWeather() {
        mViewModel.getCurrentLocationWeather(activity!!)
    }

    private fun subscribeData() {
        lifecycle.addObserver(mViewModel)

        mViewModel.weather.observe(this, androidx.lifecycle.Observer { weatherForecast ->
            val weeklyForecast = weatherForecast?.getWeeklyForecast()!!
            mWeekAdapter.updateData(weeklyForecast)
            updateWeatherInfo(weeklyForecast[0])
        })

        val errorsSub = mViewModel.errors.subscribe {
            Toast.makeText(context, "Error it", Toast.LENGTH_SHORT).show()
        }
        compositeDisposable.add(errorsSub)
    }

    private fun loadWeather() {
        val coords = AppPref.getCoords(context!!)
        if (coords != null) {
            mViewModel.updateLocation(coords.latitude, coords.longitude)
            mViewModel.getWeather()
        } else {
            getCurrentLocationWeather()
        }
    }

    private fun resetViews() {
        textDayOfWeek.text = ""
        textHumidity.text = ""
        textWind.text = ""
        textTemperature.text = ""
    }
}
