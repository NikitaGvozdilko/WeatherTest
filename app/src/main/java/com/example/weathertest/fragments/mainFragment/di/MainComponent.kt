package com.example.weathertest.fragments.mainFragment.di

import com.example.weathertest.MainViewModel
import com.example.weathertest.adapters.DayAdapter
import com.example.weathertest.adapters.WeekAdapter
import dagger.Component

@SingleScope
@Component(modules = [MainModule::class])
interface MainComponent {
    fun getWeekAdapter(): WeekAdapter
    fun getDayAdapter(): DayAdapter
    fun getViewModel(): MainViewModel
}