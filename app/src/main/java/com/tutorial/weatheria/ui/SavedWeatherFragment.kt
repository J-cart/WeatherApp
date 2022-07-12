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
import com.tutorial.weatheria.arch.WeatherViewModel
import com.tutorial.weatheria.databinding.FragmentSavedWeatherBinding

@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.N)
class SavedWeatherFragment : Fragment() {
    private var _binding: FragmentSavedWeatherBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WeatherViewModel by activityViewModels()
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

        binding.forecastRV.adapter = adapter
        if (isConnected()) {
            onlineLogic()
        } else {
            offlineLogic()
        }

    }

    private fun onlineLogic() {
        val list = viewModel.list
        viewModel.doOnlineSavedOperation(list)
        viewModel.onlineSavedWeatherResultTest.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Successful -> {
                    binding.progressBar.isVisible = false
                    adapter.submitList(response.data)
                }
                is Resource.Failure -> {
                    binding.progressBar.isVisible = false
                    binding.weekTV.text = response.msg
                }
                is Resource.Loading -> {
                    binding.progressBar.isVisible = true
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
                }
                is Resource.Failure -> {
                    binding.progressBar.isVisible = false
                    val text = "${response.msg}--Weather returns null, check network and refresh"
                    makeToast(text)
                }
                is Resource.Loading -> {
                    binding.progressBar.isVisible = true
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
        val networkManager =
            activity?.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        val network = networkManager.getNetworkCapabilities(networkManager.activeNetwork)
        return network?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true ||
                network?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true ||
                network?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true
    }
}