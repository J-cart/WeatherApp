package com.tutorial.weatheria.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tutorial.weatheria.HourAdapter
import com.tutorial.weatheria.Resource
import com.tutorial.weatheria.arch.WeatherViewModel
import com.tutorial.weatheria.databinding.FragmentCurrentWeatherBinding
import java.text.SimpleDateFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.N)
@SuppressLint("MissingPermission")
class CurrentWeatherFragment : Fragment() {
    private var _binding: FragmentCurrentWeatherBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WeatherViewModel by activityViewModels()

    private val adapter: HourAdapter by lazy { HourAdapter() }
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var userLocation: String? = null
    private lateinit var networkManager: ConnectivityManager


    private fun checkDateFormat(time: Long): String {
        val dateFormat = SimpleDateFormat("yy-MM-dd hh:mm:ss", Locale.getDefault())
        return dateFormat.format(time)
    }

    // 0:: CHECK PERMISSIONS AT ALL COST...
    // 1 :: check if gps is enables or network provider is enabled----> let it returns true or false
    //2 :: if 1 returns true  get Last Location -- if its null ---request new one ->3
    // 3:: request Location
    private fun checkGps(): Boolean {
        val manager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER) || manager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private val request = LocationRequest.create().apply {
        interval = 1000L
        fastestInterval = 5000L
        priority = LocationRequest.PRIORITY_LOW_POWER
    }
    private val callBack = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            val location = p0.lastLocation
            Log.d("LOCATING", "Location CallBAck $location")
            binding.todayTv.text = "$location"
            userLocation = "${location.latitude},${location.longitude}"
        }
    }

    private fun requestLocation() {
        fusedLocationProviderClient.requestLocationUpdates(
            request,
            callBack,
            Looper.getMainLooper()
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCurrentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())//FusedLocationProviderClient(requireContext())

        binding.weatherIcon.setOnClickListener {
            val direction =
                CurrentWeatherFragmentDirections.actionCurrentWeatherFragmentToForecastWeatherDetailFragment()
            findNavController().navigate(direction)
        }
        binding.locationTV.setOnClickListener {
            val navigate =
                CurrentWeatherFragmentDirections.actionCurrentWeatherFragmentToSearchLocationFragment()
            findNavController().navigate(navigate)
        }
        binding.windText.setOnClickListener {
            val navigate = CurrentWeatherFragmentDirections.actionCurrentWeatherFragmentToSavedWeatherFragment()
            findNavController().navigate(navigate)
        }
        binding.recentsRv.adapter = adapter

        networkManager =
            activity?.getSystemService(ConnectivityManager::class.java) as ConnectivityManager

        // networkCapabilities = networkManager.getNetworkCapabilities(networkManager.activeNetwork)

        doNetworkOperation()//wholeLogicTest()
        binding.todayTv.setOnClickListener {

            doNetworkOperation()//wholeLogicTest()
        }

    }

    override fun onPause() {
        super.onPause()
        fusedLocationProviderClient.removeLocationUpdates(callBack)
    }

    override fun onResume() {
        super.onResume()
        //TODO check network state too
        if (!checkGps()) {
            val text = "No permission, activate and refresh"
            makeToast(text)
            return
        }
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())//FusedLocationProviderClient(requireContext())

    }

    private fun setUpUi(viewModel: WeatherViewModel, location: String) {
        viewModel.updateWeather(location)
        viewModel.weatherForecast.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Successful -> {
                    binding.progressBar.isVisible = false
                    val current = response.data?.current
                    binding.apply {
                        weatherIcon.load("http:${current?.condition?.icon}")
                        locationTV.text = response.data?.location?.name// current?.lastUpdated
                        dayTv.text = current?.condition?.text
                        val line = checkDateFormat(System.currentTimeMillis())
                        Log.d("TIME-ING", "$line")
                        dateTv.text = checkDateFormat(System.currentTimeMillis())
                        tempText.text = current?.tempC.toString()
                        humidityText.text = current?.humidity.toString()
                        windText.text = current?.windMph.toString()
                    }
                    // response.data?.forecast?.forecastday
                    adapter.submitList(response.data?.forecast?.forecastday?.get(0)?.hour)

                }
                is Resource.Failure -> {
                    binding.progressBar.isVisible = false
                    binding.locationTV.text = response.msg
                }
                is Resource.Loading -> {
                    //binding.locationTV.text = "LIST IS FRIGGING EMPTY"
                    binding.progressBar.isVisible = true
                }
                else -> Unit
            }
        }
    }


    private val requestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            when {
                it.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    runGpsAndMainOperation()
                }
                it.getOrDefault(Manifest.permission.ACCESS_BACKGROUND_LOCATION, false) -> {
                    runGpsAndMainOperation()
                }
                it.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    runGpsAndMainOperation()
                }
                else -> {
                    val title = "ACCEPT PERMISSION REQUEST"
                    val text = "You need to allow permission for app to work "
                    makeSnack(title, text)
                }
            }
        }

    private fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            requestLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun checkPerms(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val is1 = ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            val is2 = ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            return is1 && is2
        } else {
            return ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }
    }


    private fun assertPerms() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setMessage("You need to allow permission for app to work  go to settings to enable permission then refresh app")
            setTitle("ACCEPT PERMISSION REQUEST")
            setPositiveButton("OK") { dialogInterface, int ->
                requestLocationPermission()
                dialogInterface.dismiss()
            }
            create()
            show()
        }
    }

    private fun runGpsAndMainOperation() {
        if (checkGps()) {
            fusedLocationProviderClient.lastLocation.addOnCompleteListener(
                OnCompleteListener {
                    if (it.result == null) {
                        requestLocation()
                    } else {
                        Log.d("LOCATING", "LastLocation()  ${it.result}")
                        binding.todayTv.text =
                            "${it.result.latitude},${it.result.longitude}"
                        userLocation = "${it.result.latitude},${it.result.longitude}"
                        setUpUi(viewModel, userLocation!!)
                    }

                })
        } else {
            val title =
                "You need to allow permission for app to work  go to settings to enable permission"
            val text = "ON GPS BRUH"
            makeSnack(title, text)
        }
    }


    private fun wholeLogic() {
        when (checkPerms()) {
            true -> {
                runGpsAndMainOperation()
            }
            false -> {
                assertPerms()
            }
        }
    }

    private fun doNetworkOperation() {
        if (isConnected()) {
            wholeLogic()
        } else {
            offlineWholeLogic()
        }
    }

    private fun offlineWholeLogic() {
        viewModel.reportDbSitu()
        viewModel.situFrmDab.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Successful -> {
                    val current = response.data?.current
                    binding.apply {
                        progressBar.visibility = View.GONE
                        todayTv.isClickable = true
                        weatherIcon.load("http:${current?.condition?.icon}")
                        locationTV.text =
                            response.data?.location?.name// current?.lastUpdated
                        dayTv.text = current?.condition?.text
                        dateTv.text = checkDateFormat(System.currentTimeMillis())
                        tempText.text = current?.tempC.toString()
                        humidityText.text = current?.humidity.toString()
                        windText.text = current?.windMph.toString()
                        adapter.submitList(response.data?.forecast?.forecastday?.get(0)?.hour)
                    }
                }
                is Resource.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    binding.todayTv.isClickable = true
                    val text = "${response.msg}--Weather returns null, check network and refresh"
                    makeToast(text)
                }
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    val text = "Loading , Alaye calm down small na"
                    makeToast(text)
                    binding.todayTv.isClickable = false
                }
                else -> Unit
            }
        }
    }

    private fun isConnected(): Boolean {
        val network = networkManager.getNetworkCapabilities(networkManager.activeNetwork)
        return network?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true ||
                network?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true ||
                network?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true
    }

    private fun makeToast(text: String) {
        Toast.makeText(
            requireContext(),
            text,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun makeSnack(title: String, text: String) {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setMessage(text)
            setTitle(title)
            setPositiveButton("OK") { dialogInterface, int ->
                dialogInterface.dismiss()
            }
            create()
            show()
        }
    }
}

