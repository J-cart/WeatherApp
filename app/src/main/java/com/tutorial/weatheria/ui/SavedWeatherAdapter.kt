package com.tutorial.weatheria.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tutorial.weatheria.R
import com.tutorial.weatheria.databinding.TestingRecentViewHolderBinding
import com.tutorial.weatheria.network_and_data_models.SavedWeather
import com.tutorial.weatheria.network_and_data_models.SearchLocationResponseItem

class SavedWeatherAdapter:ListAdapter<SavedWeather,SavedWeatherAdapter.ViewHolder>(diffObject) {
    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val binding = TestingRecentViewHolderBinding.bind(view)
        fun bind(savedWeather: SavedWeather){
            binding.recentName.text = savedWeather.toString()
        }

    }

    companion object {
        val diffObject = object : DiffUtil.ItemCallback<SavedWeather>() {
            override fun areItemsTheSame(oldItem: SavedWeather, newItem: SavedWeather): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: SavedWeather, newItem: SavedWeather): Boolean {
                return oldItem.id  == newItem.id && oldItem.location  == newItem.location
            }
        }
    }

    private var listener:((SavedWeather)->Unit)? = null
    fun adapterClick(listener:(SavedWeather)->Unit){
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.testing_recent_view_holder,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pos = getItem(position)
        if (pos!=null)
            holder.bind(pos)
    }
}