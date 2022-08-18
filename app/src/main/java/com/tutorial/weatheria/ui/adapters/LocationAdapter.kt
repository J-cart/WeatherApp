package com.tutorial.weatheria.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tutorial.weatheria.R
import com.tutorial.weatheria.databinding.SearchLocationViewHolderBinding
import com.tutorial.weatheria.network_and_data_models.SearchLocationResponseItem

class LocationAdapter:ListAdapter<SearchLocationResponseItem, LocationAdapter.ViewHolder>(diffObject) {
    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val binding =SearchLocationViewHolderBinding.bind(view)
        fun bind(location: SearchLocationResponseItem){
            binding.nameTv.text = location.name
            binding.regionTv.text = location.region
            binding.countryTv.text = location.country
            binding.root.setOnClickListener{
                listener?.let { it1 -> it1(location) }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_location_view_holder,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pos = getItem(position)
        if (pos != null)
            holder.bind(pos)
    }
    companion object {
        val diffObject = object : DiffUtil.ItemCallback<SearchLocationResponseItem>() {
            override fun areItemsTheSame(oldItem: SearchLocationResponseItem, newItem: SearchLocationResponseItem): Boolean {
                return oldItem.name == newItem.name
            }
            override fun areContentsTheSame(oldItem: SearchLocationResponseItem, newItem: SearchLocationResponseItem): Boolean {
                return oldItem.lat  == newItem.lat && oldItem.lon  == newItem.lon
            }
        }
    }

    private var listener:((SearchLocationResponseItem)->Unit)? = null
    fun adapterClick(listener:(SearchLocationResponseItem)->Unit){
        this.listener = listener
    }
}