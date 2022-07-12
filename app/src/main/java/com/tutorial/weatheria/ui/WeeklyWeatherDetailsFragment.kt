package com.tutorial.weatheria.ui

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import com.tutorial.weatheria.ForecastAdapter
import com.tutorial.weatheria.HourAdapter
import com.tutorial.weatheria.R
import com.tutorial.weatheria.Resource
import com.tutorial.weatheria.arch.WeatherViewModel
import com.tutorial.weatheria.databinding.FragmentCurrentWeatherBinding
import com.tutorial.weatheria.databinding.FragmentWeeklyWeatherDetailsBinding

@RequiresApi(Build.VERSION_CODES.N)
class WeeklyWeatherDetailsFragment : Fragment() {
    private var _binding: FragmentWeeklyWeatherDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WeatherViewModel by activityViewModels()
    // val adapter: ForecastAdapter by lazy { ForecastAdapter() }
    val adapter: ForecastAdapter by lazy { ForecastAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWeeklyWeatherDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.forecastRV.adapter = adapter
        if (isConnected()){
            onlineLogic()

        }else{
            offlineLogic()
        }
    }

    private fun onlineLogic(){
        viewModel.weatherForecast.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Successful -> {
                    adapter.submitList(response.data?.forecast?.forecastday)

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
    private fun offlineLogic() {
        viewModel.reportDbSitu()
        viewModel.situFrmDab.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Successful -> {
                    adapter.submitList(response.data?.forecast?.forecastday)

                }
                is Resource.Failure -> {
                    val text = "${response.msg}--Weather returns null, check network and refresh"
                    makeToast(text)
                }
                is Resource.Loading -> {
                    val text = "Alaye wait na abi u wan collect"
                    makeToast(text)
                }
                else -> Unit
            }
        }
    }
    private fun makeToast(text: String) {
        Toast.makeText(
            requireContext(),
            text,
            Toast.LENGTH_SHORT
        ).show()
    }


    private fun isConnected(): Boolean {
        val networkManager = activity?.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        val network = networkManager.getNetworkCapabilities(networkManager.activeNetwork)
        return network?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true ||
                network?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true ||
                network?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true
    }
}