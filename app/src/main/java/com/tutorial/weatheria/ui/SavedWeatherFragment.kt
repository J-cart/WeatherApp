package com.tutorial.weatheria.ui

import android.annotation.SuppressLint
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.tutorial.weatheria.Resource
import com.tutorial.weatheria.ui.adapters.SavedWeatherAdapter
import com.tutorial.weatheria.arch.WeatherViewModel
import com.tutorial.weatheria.databinding.FragmentSavedWeatherBinding
import com.tutorial.weatheria.isConnected
import com.tutorial.weatheria.makeToast

@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.N)
class SavedWeatherFragment : Fragment() {
    private var _binding: FragmentSavedWeatherBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WeatherViewModel by activityViewModels()
    private lateinit var networkManager:ConnectivityManager
    private val adapter: SavedWeatherAdapter by lazy { SavedWeatherAdapter() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSavedWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         networkManager =
            activity?.getSystemService(ConnectivityManager::class.java) as ConnectivityManager

        binding.forecastRV.adapter = adapter
        binding.forecastRV.setHasFixedSize(true)
        wholeLogic()
        adapter.adapterClick {
            viewModel.deleteSavedWeather(it).also { adapter.submitList(emptyList()) }
            wholeLogic()
        }

    }
    private fun wholeLogic(){
        if (isConnected(networkManager)) {
            onlineLogic()
        } else {
            offlineLogic()
        }
    }

    private fun onlineLogic() {
        viewModel.doOnlineSavedOperation()
        viewModel.onlineSavedWeatherResultTest.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Successful -> {
                    binding.progressBar.isVisible = false
                    adapter.submitList(response.data)
                    binding.shimmerSaved.root.isVisible = false
                }
                is Resource.Failure -> {
                    binding.progressBar.isVisible = false
//                    binding.weekTV.text = response.msg
                }
                is Resource.Loading -> {
                   // binding.progressBar.isVisible = true
                    binding.shimmerSaved.root.isVisible = true
                }
                else -> Unit
            }
        }
    }

    private fun offlineLogic() {
        viewModel.reportSavedDbSitu()
        viewModel.offLineSavedWeatherResultTest.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Successful -> {
                    adapter.submitList(response.data)
                    binding.progressBar.isVisible = false
                    binding.shimmerSaved.root.isVisible = false
                }
                is Resource.Failure -> {
                    binding.progressBar.isVisible = false
//                    binding.weekTV.text = "${response.msg} ,check network/Db"
                    val text = "${response.msg}, check network and refresh"
                    makeToast(text)
                }
                is Resource.Loading -> {
                    //binding.progressBar.isVisible = true
                    binding.shimmerSaved.root.isVisible = true
                }
                else-> Unit
            }
        }
    }


}