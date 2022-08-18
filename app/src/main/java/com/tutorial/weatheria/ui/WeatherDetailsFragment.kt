package com.tutorial.weatheria.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import coil.load
import com.tutorial.weatheria.ui.adapters.HourAdapter
import com.tutorial.weatheria.R
import com.tutorial.weatheria.Resource
import com.tutorial.weatheria.arch.WeatherViewModel
import com.tutorial.weatheria.databinding.FragmentWeatherDetailsBinding
import com.tutorial.weatheria.makeToast
import com.tutorial.weatheria.network_and_data_models.SavedWeather
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*


class WeatherDetailsFragment : Fragment() {
    private var _binding: FragmentWeatherDetailsBinding? = null
    private val binding get() = _binding!!
    private val args: WeatherDetailsFragmentArgs by navArgs()
    private val viewModel: WeatherViewModel by activityViewModels()
    private val adapter: HourAdapter by lazy { HourAdapter() }
    private lateinit var location: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWeatherDetailsBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        location =
            "${args.locationDetails.lat},${args.locationDetails.lon}" //or betterstill pass the name
        //requireActivity().actionBar?.title = args.locationDetails.name
        binding.recentsRv.adapter = adapter
        binding.recentsRv.setHasFixedSize(true)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.savingEvents.collect { event ->
                when (event) {
                    is WeatherViewModel.Events.Successful -> {
                        makeToast("Location Saved")
                    }
                    is WeatherViewModel.Events.Failure -> {
                        makeToast("Saved location size cannot exceed 5")
                    }
                }
            }
        }


        setUpUi(viewModel, location)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.refresh_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.refreshPage -> setUpUi(viewModel, location)
        }
        return super.onOptionsItemSelected(item)
    }


    private fun setUpUi(viewModel: WeatherViewModel, location: String) {
        viewModel.updateWeatherSearchedLocation(location)

        viewModel.searchLocationWeatherResult.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Successful -> {
                    binding.progressBar.isVisible = false
                    var current = response.data?.current
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
                        adapter.submitList(response.data?.forecast?.forecastday?.get(0)?.hour)
                        response.data?.forecast?.forecastday?.get(0)?.hour?.get(0)?.condition?.icon
                        favorite.setOnClickListener {
                            val save = SavedWeather(
                                location = response.data?.location,
                                current = response.data?.current
                            )
                            viewModel.saveToDb(save)

                        }
                    }
                }
                is Resource.Failure -> {
                    binding.progressBar.isVisible = false
                    binding.locationTV.text = response.msg
                }
                is Resource.Empty -> {
                    binding.progressBar.isVisible = false
                    binding.locationTV.text = "LIST IS FRIGGING EMPTY"
                }
                is Resource.Loading->{
                    binding.progressBar.isVisible = true
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