package com.example.weathertest.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weathertest.R
import com.example.weathertest.api.model.Weather
import com.example.weathertest.utils.Utils
import kotlinx.android.synthetic.main.item_day_temperature.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class DayAdapter() : RecyclerView.Adapter<DayAdapter.ViewHolder>() {
    private var weatherList: List<Weather>? = null
    private var format: DateFormat? = null

    init {
        format = SimpleDateFormat("HH")
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day_temperature, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = weatherList?.size ?: 0


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (weatherList != null) {
            holder.apply {
                textTemperature?.text = weatherList!![position].temperatureStr
                textTimeHours?.text = format?.format(weatherList!![position].date.time)
                imageWeather?.setImageResource(
                    Utils.getWeatherIcon(
                        weatherList!![position].weatherDescription!!,
                        weatherList!![position].date.get(Calendar.HOUR_OF_DAY)
                    )
                )
            }
        }
    }

    fun updateData(list: List<Weather>) {
        this.weatherList = list
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textTemperature: TextView? = null
        var imageWeather: ImageView? = null
        var textTimeHours: TextView? = null

        init {
            textTemperature = itemView.textTemperature
            imageWeather = itemView.imageWeather
            textTimeHours = itemView.textTimeHours
        }
    }
}
