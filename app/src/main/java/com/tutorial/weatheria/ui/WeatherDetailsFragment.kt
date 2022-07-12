package com.tutorial.weatheria.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import coil.load
import com.tutorial.weatheria.HourAdapter
import com.tutorial.weatheria.Resource
import com.tutorial.weatheria.arch.WeatherViewModel
import com.tutorial.weatheria.databinding.FragmentWeatherDetailsBinding
import com.tutorial.weatheria.network_and_data_models.SavedWeather
import java.text.SimpleDateFormat
import java.util.*


class WeatherDetailsFragment : Fragment() {
    private var _binding: FragmentWeatherDetailsBinding? = null
    private val binding get() = _binding!!
    private val args: WeatherDetailsFragmentArgs by navArgs()
    private val viewModel: WeatherViewModel by activityViewModels()
    private val adapter: HourAdapter by lazy { HourAdapter() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWeatherDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val location = "${args.locationDetails.lat},${args.locationDetails.lon}" //or betterstill pass the name
        setUpUi(viewModel, location)

    }

    private fun setUpUi(viewModel: WeatherViewModel, location: String) {
        viewModel.updateWeatherSearchedLocation(location)
        binding.recentsRv.adapter = adapter
        viewModel.searchLocationWeatherResult.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Successful -> {
                    val current = response.data?.current
                    binding.apply {
                        weatherIcon.load("http:${current?.condition?.icon}")
                        locationTV.text = response.data?.location?.name// current?.lastUpdated
                        dayTv.text = current?.condition?.text
                        val line = checkDateFormat(System.currentTimeMillis())
                        Log.d("TIMEING", "$line")
                        dateTv.text = checkDateFormat(System.currentTimeMillis())
                        tempText.text = current?.tempC.toString()
                        humidityText.text = current?.humidity.toString()
                        windText.text = current?.windMph.toString()
                        adapter.submitList(  response.data?.forecast?.forecastday?.get(0)?.hour)
                        response.data?.forecast?.forecastday?.get(0)?.hour?.get(0)?.condition?.icon
                        todayTv.setOnClickListener {
                            val save = SavedWeather(
                                location = response.data?.location,
                                current = response.data?.current
                            )
                            //TODO set max size to 10 ..if > 10 restrict adding to db
                            viewModel.insertSavedWeather(save)
                        }
                    }
                }
                is Resource.Failure -> {
                    binding.locationTV.text = response.msg
                }
                is Resource.Empty -> {
                    binding.locationTV.text = "LIST IS FRIGGING EMPTY"
                }
                else -> Unit
            }
        }
    }

    private fun checkDateFormat(time: Long): String {
        val dateFormat = SimpleDateFormat("yy-MM-dd hh:mm:ss", Locale.getDefault())
        return dateFormat.format(time)
    }
}