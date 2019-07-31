package com.example.weathertest.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.weathertest.R
import com.example.weathertest.api.model.Weather
import com.example.weathertest.utils.Utils
import kotlinx.android.synthetic.main.item_day_of_week.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class WeekAdapter(private val context: Context,
                  private val onDayItemClickListener: OnItemClickListener) : RecyclerView.Adapter<WeekAdapter.ViewHolder>() {
    private var weatherList: List<Weather>? = null
    private var format: DateFormat? = null
    private var selectedWeather: Weather? = null

    init {
        format = SimpleDateFormat("EE")
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_day_of_week, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = weatherList?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(weatherList != null) {
            holder.apply {
                if (weatherList!![position] == (selectedWeather)) rootItem?.setBackgroundResource(R.color.selectedColor)
                else rootItem?.setBackgroundResource(android.R.color.white)
                textDayOfWeek?.text = format?.format(weatherList!![position].date.time)
                textTemperature?.text = weatherList!![position].temperatureRange
                holder.rootItem?.setOnClickListener {
                    selectedWeather = weatherList!![position]
                    onDayItemClickListener.onClick(selectedWeather!!)
                    notifyDataSetChanged()
                }
                if (weatherList!![position].weatherDescription != null) {
                    imageWeather?.setImageResource(
                            Utils.getWeatherIcon(weatherList!![position].weatherDescription!!,
                                    weatherList!![position].date.get(Calendar.HOUR_OF_DAY)))
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

    interface OnItemClickListener {
        fun onClick(day: Weather)
    }
}