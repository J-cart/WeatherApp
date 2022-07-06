package com.tutorial.weatheria

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.tutorial.weatheria.databinding.HourlyViewHolderBinding
import com.tutorial.weatheria.network_and_data_models.Forecastday
import java.text.SimpleDateFormat
import java.util.*


class ForecastAdapter: ListAdapter<Forecastday,ForecastAdapter.ViewHolder>(diffObject) {
    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val binding = HourlyViewHolderBinding.bind(view)
        fun bind(forecastday: Forecastday){
            binding.apply {
                weathericon.load("http:${forecastday.day.condition.icon}"){
                    error(R.drawable.ic_launcher_background)
                    placeholder(R.drawable.ic_launcher_foreground)
                }

                //TODO
                val dateFormat = SimpleDateFormat("yy-MM-dd",Locale.getDefault())
                val text = dateFormat.parse("${forecastday.date}")
                val dateIndex = text.toString().split(" ")
                tempC.text = "${forecastday.day.avgtempC} C``"
                tempF.text = forecastday.day.condition.text
                dateTV.text = forecastday.date // + "${dateIndex[0]..dateIndex[2]}"
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.hourly_view_holder,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pos = getItem(position)
        if (pos != null)
            holder.bind(pos)
    }

    companion object {
        val diffObject = object : DiffUtil.ItemCallback<Forecastday>() {
            override fun areItemsTheSame(oldItem: Forecastday, newItem: Forecastday): Boolean {
                return oldItem.date == newItem.date
            }
            override fun areContentsTheSame(oldItem: Forecastday, newItem: Forecastday): Boolean {
                return oldItem.date == newItem.date
            }
        }
    }
}