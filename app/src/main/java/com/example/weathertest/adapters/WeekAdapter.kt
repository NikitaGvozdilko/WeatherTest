package com.example.weathertest.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.weathertest.R
import com.example.weathertest.api.model.Weather
import com.example.weathertest.utils.Utils
import kotlinx.android.synthetic.main.item_day_of_week.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class WeekAdapter(private var selectedDay: MutableLiveData<Weather>, private val glide: RequestManager) : RecyclerView.Adapter<WeekAdapter.ViewHolder>() {
    private var weatherList: List<Weather>? = null
    private var format: DateFormat = SimpleDateFormat("EE")
    private var selectedWeather: Weather? = null

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day_of_week, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = weatherList?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (weatherList != null) {
            holder.apply {
                val weather = weatherList!![position]
                if (weather == (selectedWeather)) rootItem?.setBackgroundResource(R.color.selectedColor)
                else rootItem?.setBackgroundResource(android.R.color.white)
                textDayOfWeek?.text = format.format(weather.date.time)
                textTemperature?.text = weather.temperatureRange
                holder.rootItem?.setOnClickListener {
                    selectedWeather = weather
                    selectedDay.value = selectedWeather
                    notifyDataSetChanged()
                }
                if (weather.weatherDescription != null) {
                    glide.load(Utils.getWeatherIcon(
                            weather.weatherDescription!!,
                            weather.date.get(Calendar.HOUR_OF_DAY)))
                            .into(imageWeather!!)
                }
            }
        }
    }

    fun updateData(list: List<Weather>) {
        this.weatherList = list
        selectedWeather = weatherList!![0]
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var rootItem: View? = null
        var textDayOfWeek: TextView? = null
        var textTemperature: TextView? = null
        var imageWeather: ImageView? = null

        init {
            rootItem = itemView
            textDayOfWeek = itemView.textDayOfWeek
            textTemperature = itemView.textTemperature
            imageWeather = itemView.imageWeather
        }
    }
}