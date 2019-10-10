package com.example.weathertest.fragments.mainFragment.di

import android.content.Context
import android.location.LocationManager
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.weathertest.MainViewModel
import com.example.weathertest.WeatherManager
import com.example.weathertest.adapters.DayAdapter
import com.example.weathertest.adapters.WeekAdapter
import com.example.weathertest.api.WeatherManagerImpl
import com.example.weathertest.api.model.Weather
import com.example.weathertest.api.model.WeatherForecast
import com.example.weathertest.fragments.mainFragment.IMainView
import dagger.Module
import dagger.Provides

@Module
class MainModule(@get:Provides val fragmentActivity: FragmentActivity, @get:Provides val view: IMainView) {

    @SingleScope
    @Provides
    fun provideGlide(fragmentActivity: FragmentActivity): RequestManager {
        return Glide.with(fragmentActivity)
    }

    @SingleScope
    @Provides
    fun provideWeekAdapter(fragmentActivity: FragmentActivity, glide: RequestManager, view: IMainView): WeekAdapter {
        val selectedDay = MutableLiveData<Weather>()
        selectedDay.observe(fragmentActivity, Observer { day ->
            view.updateWeatherInfo(day)
        })
        return WeekAdapter(selectedDay, glide)
    }

    @SingleScope
    @Provides
    fun provideDayAdapter(glide: RequestManager): DayAdapter {
        return DayAdapter(glide)
    }

    @Provides
    fun provideWeatherManager(): WeatherManager {
        return WeatherManagerImpl()
    }

    @SingleScope
    @Provides
    fun provideViewModel(context: FragmentActivity, weatherManager: WeatherManager): MainViewModel {
        val mLocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        val factory = MainViewModel.CustomViewModelFactory(weatherManager, mLocationManager!!)
        val viewModel = ViewModelProviders.of(context, factory).get(MainViewModel::class.java)
        return viewModel
    }
}