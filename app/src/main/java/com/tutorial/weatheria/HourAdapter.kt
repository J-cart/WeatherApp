package com.tutorial.weatheria

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.tutorial.weatheria.databinding.TestingTodayViewHolderBinding
import com.tutorial.weatheria.databinding.TestingViewHolderBinding
import com.tutorial.weatheria.network_and_data_models.Hour

class HourAdapter: ListAdapter<Hour, HourAdapter.ViewHolder2>(diffObject) {
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = TestingViewHolderBinding.bind(view)
        fun bind(hour: Hour){
            binding.apply {
                currentWdate.text = hour.time
                currentWcondition.text =  hour.condition.text
                currentWtempF.text = "${hour.tempF} F``"
                currentWtempC.text = "${hour.tempC} C``"
            }
        }

    }
inner class ViewHolder2(view: View): RecyclerView.ViewHolder(view) {
        val binding = TestingTodayViewHolderBinding.bind(view)
        fun bind(hour: Hour){
            binding.apply {
                weathericon.load("http:${hour.condition.icon}"){
                    error(R.drawable.ic_launcher_background)
                    placeholder(R.drawable.ic_launcher_foreground)
                }
                val hourIndex = hour.time.split(" ")
                Log.d("response34","$hourIndex")
                tempC.text = "${hour.tempC} C``"
                tempF.text = "${hourIndex[1]} "
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder2 {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.testing_today_view_holder,parent,false)
        //val view2 = LayoutInflater.from(parent.context).inflate(R.layout.testing_view_holder,parent,false)ViewHolder(view)
        return ViewHolder2(view)
    }

    override fun onBindViewHolder(holder: ViewHolder2, position: Int) {
        val pos = getItem(position)
        if (pos != null)
            holder.bind(pos)
    }

    companion object {
        val diffObject = object : DiffUtil.ItemCallback<Hour>() {
            override fun areItemsTheSame(oldItem: Hour, newItem: Hour): Boolean {
                return oldItem.time == newItem.time
            }
            override fun areContentsTheSame(oldItem: Hour, newItem: Hour): Boolean {
                return oldItem.timeEpoch == newItem.timeEpoch
            }
        }
    }
}