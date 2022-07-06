package com.tutorial.weatheria.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import coil.load
import com.tutorial.weatheria.HourAdapter
import com.tutorial.weatheria.R
import com.tutorial.weatheria.Resource
import com.tutorial.weatheria.arch.WeatherViewModel
import com.tutorial.weatheria.databinding.FragmentCurrentWeatherBinding
import com.tutorial.weatheria.databinding.FragmentDailyWeatherDeatailsBinding
import kotlin.time.Duration.Companion.minutes

class DailyWeatherDeatailsFragment : Fragment() {
    private var _binding: FragmentDailyWeatherDeatailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WeatherViewModel by activityViewModels()
    // val adapter: ForecastAdapter by lazy { ForecastAdapter() }
    val adapter: HourAdapter by lazy { HourAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDailyWeatherDeatailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.forecastRV.adapter = adapter
        viewModel.weatherForecast.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Successful -> {
                    adapter.submitList(response.data?.forecast?.forecastday?.get(0)?.hour)
                }
                is Resource.Failure -> {
                    binding.dayTV.text = response.msg
                }
                is Resource.Empty -> {
                    binding.dayTV.text = "LIST IS FRIGGING EMPTY"
                }
                else -> Unit
            }
        }
    }
}