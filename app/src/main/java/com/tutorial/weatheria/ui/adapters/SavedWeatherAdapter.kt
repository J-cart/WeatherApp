package com.tutorial.weatheria.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.tutorial.weatheria.R
import com.tutorial.weatheria.databinding.SavedWeatherViewholderBinding
import com.tutorial.weatheria.network_and_data_models.SavedWeather
import java.text.SimpleDateFormat
import java.util.*

class SavedWeatherAdapter : ListAdapter<SavedWeather, SavedWeatherAdapter.ViewHolder>(diffObject) {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = SavedWeatherViewholderBinding.bind(view)
        fun bind(savedWeather: SavedWeather) {
            binding.apply {
                val dateFormat = SimpleDateFormat("yy-MM-dd hh:mm", Locale.getDefault())
                val text = savedWeather.current?.lastUpdated?.let { dateFormat.parse(it) }
                val dateIndex = text.toString().split(" ")

                Log.d("sme", "${savedWeather.current?.lastUpdated}")
                Log.d("sme", "$dateIndex")

                dateTV.text =
                    savedWeather.current?.lastUpdated?.let { formatDate(it) }//savedWeather.current?.lastUpdated
                location.text = savedWeather.location?.name
                condition.text = savedWeather.current?.condition?.text
                tempC.text = "${savedWeather.current?.tempC}℃"
                weathericon.load("http://${savedWeather.current?.condition?.icon}") {
                    error(R.drawable.ic_launcher_background)
                    placeholder(R.drawable.loading_img)
                }

            }
            binding.root.setOnLongClickListener {
                listener?.invoke(savedWeather)
                true
            }

        }

    }

    companion object {
        val diffObject = object : DiffUtil.ItemCallback<SavedWeather>() {
            override fun areItemsTheSame(oldItem: SavedWeather, newItem: SavedWeather): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: SavedWeather, newItem: SavedWeather): Boolean {
                return oldItem.id == newItem.id && oldItem.location == newItem.location
            }
        }
    }

    private var listener: ((SavedWeather) -> Unit)? = null
    fun adapterClick(listener: (SavedWeather) -> Unit) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.saved_weather_viewholder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pos = getItem(position)
        if (pos != null)
            holder.bind(pos)
    }

    private fun formatDate(date: String): String {
        val dateFormat = SimpleDateFormat("yy-MM-dd hh:mm", Locale.getDefault())
        val dateText = dateFormat.parse(date)
        val dateIndex = dateText?.toString()?.split(" ")
        val year = "${dateIndex?.get(2)}/${dateIndex?.get(5)?.get(2)}${dateIndex?.get(5)?.get(3)}"
        return "${dateIndex?.get(0)} - $year"
    }
}