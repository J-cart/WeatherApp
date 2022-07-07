package com.tutorial.weatheria.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.tutorial.weatheria.HourAdapter
import com.tutorial.weatheria.Resource
import com.tutorial.weatheria.arch.WeatherViewModel
import com.tutorial.weatheria.databinding.FragmentCurrentWeatherBinding
import java.text.SimpleDateFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.N)
class CurrentWeatherFragment : Fragment() {
    private var _binding: FragmentCurrentWeatherBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WeatherViewModel by activityViewModels()

    // val adapter: ForecastAdapter by lazy { ForecastAdapter() }
    private val adapter: HourAdapter by lazy { HourAdapter() }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var userLocation: String? = null

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

    @SuppressLint("MissingPermission")
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
        binding.recentsRv.adapter = adapter

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())//FusedLocationProviderClient(requireContext())
        assertPerms()
        userLocation?.let { it1 -> setUpUi2(viewModel, it1) }
        //wholeLogic()
        binding.todayTv.setOnClickListener { //TODO THIS REFRESHES THE APP
            if (!checkPerms()) {
                Toast.makeText(
                    requireContext(),
                    "No permission, activate and refresh",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            assertPerms()
            userLocation?.let { it1 -> setUpUi2(viewModel, it1) }
            //wholeLogic()
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationProviderClient.removeLocationUpdates(callBack)
    }

    override fun onResume() {
        super.onResume()
        if (!checkGps()) {
            Toast.makeText(
                requireContext(),
                "No permission, activate and refresh",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())//FusedLocationProviderClient(requireContext())

    }

/*
    private fun setUpUi(viewModel: WeatherViewModel, location: String) {
        viewModel.updateWeather(location)
        viewModel.weatherForecast.observe(viewLifecycleOwner) { response ->
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
                    }
                    // response.data?.forecast?.forecastday
                    adapter.submitList(response.data?.forecast?.forecastday?.get(0)?.hour)

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
*/

    private fun setUpUi2(viewModel: WeatherViewModel, location: String) {
        viewModel.updateWeather(location)
        /*
        DIDN'T WORK SO I THOUGHT OF USING THE RESOURCE<T> AS A WRAPPER TO OBSERVE THE RESULT
        viewModel.frmDab.observe(viewLifecycleOwner) { weather ->
            Log.d("setupUi", " $weather")
            if (weather.id < 0) {
                Toast.makeText(
                    requireContext(),
                    "Weather returns null, check network and refresh",
                    Toast.LENGTH_SHORT
                ).show()
                return@observe
            }
            val current = weather.current
            binding.apply {
                weatherIcon.load("http:${current.condition.icon}")
                locationTV.text = weather.location.name// current?.lastUpdated
                dayTv.text = current.condition.text
                val line = checkDateFormat(System.currentTimeMillis())
                Log.d("TIMEING", "$line")
                dateTv.text = checkDateFormat(System.currentTimeMillis())
                tempText.text = current.tempC.toString()
                humidityText.text = current.humidity.toString()
                windText.text = current.windMph.toString()
            }
            // response.data?.forecast?.forecastday
            adapter.submitList(weather.forecast.forecastday[0].hour)

        }*/

        viewModel.situFrmDab.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Successful -> {
                    val current = response.data?.current
                    binding.apply {
                        progressBar.visibility = View.GONE
                        todayTv.isClickable = true
                        weatherIcon.load("http:${current?.condition?.icon}")
                        locationTV.text = response.data?.location?.name// current?.lastUpdated
                        dayTv.text = current?.condition?.text
                        dateTv.text = checkDateFormat(System.currentTimeMillis())
                        tempText.text = current?.tempC.toString()
                        humidityText.text = current?.humidity.toString()
                        windText.text = current?.windMph.toString()
                    }
                }
                is Resource.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    binding.todayTv.isClickable = true
                    Toast.makeText(
                        requireContext(),
                        "Weather returns null, check network and refresh",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    Toast.makeText(
                        requireContext(),
                        "Loading , Alaye calm down small na",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.todayTv.isClickable = false
                }
                else -> Unit
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun assertPerms() {
        if (!checkPerms()) {
            MaterialAlertDialogBuilder(requireContext()).apply {
                setMessage("You need to allow permission for app to work  go to settings to enable permission, Please refresh/restart the app when accepted")
                setTitle("ACCEPT PERMISSION REQUEST")
                setPositiveButton("OK") { dialogInterface, int ->
                    requestLocationPermission(requireView())
                    dialogInterface.dismiss() // or you can request permission again
                }
                create()
                show()
            }

        } else {
            //requestLocationPermission(requireView())
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
                            //?:setUpUi2(viewModel,"")
                        }
                    })
            } else {
                MaterialAlertDialogBuilder(requireContext()).apply {
                    setMessage("You need to allow permission for app to work  go to settings to enable permission")
                    setTitle("ON GPS BRUH!")
                    setPositiveButton("OK") { dialogInterface, int ->
                        dialogInterface.dismiss() // or you can request permission again
                    }
                    create()
                    show()
                }
            }
        }
    }


    /*
    THIS REGION IS FOR THE NORMAL REQUEST WITHOUT CACHING!! works fine until i got the idea of caching😭😭😭😭

    private fun assertPerms() {
        if (!checkPerms()) {
            MaterialAlertDialogBuilder(requireContext()).apply {
                setMessage("You need to allow permission for app to work  go to settings to enable permission")
                setTitle("ACCEPT PERMISSION REQUEST")
                setPositiveButton("OK") { dialogInterface, int ->
                    requestLocationPermission(requireView())
                    dialogInterface.dismiss() // or you can request permission again
                }
                create()
                show()
            }
        } else {
            requestLocationPermission(requireView()) // or do the normal task since this  is redundant
        }
    }

    @SuppressLint("MissingPermission")
    private fun wholeLogic() {
        when (checkPerms()) {
            true -> {
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
                                userLocation?.let { location ->
                                    setUpUi(viewModel, location)
                                } //?:setUpUi2(viewModel,"")
                            }
                        })
                } else {
                    MaterialAlertDialogBuilder(requireContext()).apply {
                        setMessage("You need to allow permission for app to work  go to settings to enable permission")
                        setTitle("ON GPS BRUH")
                        setPositiveButton("OK") { dialogInterface, int ->
                            dialogInterface.dismiss() // or you can request permission again
                        }
                        create()
                        show()
                    }
                }
            }
            else -> {
                binding.todayTv.text = "Check permissions"
            }
        }
    }*/


    private val requestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            when {
                it.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    requestLocationPermission(requireView())
                }
                it.getOrDefault(Manifest.permission.ACCESS_BACKGROUND_LOCATION, false) -> {
                    requestLocationPermission(requireView())
                }
                it.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    requestLocationPermission(requireView())
                }
                else -> {

                    MaterialAlertDialogBuilder(requireContext()).apply {
                        setMessage("You need to allow permission for app to work ")
                        setTitle("ACCEPT PERMISSION REQUEST")
                        setPositiveButton("OK") { dialogInterface, Int ->
                            dialogInterface.dismiss() // or you can request permission again
                        }
                        create()
                        show()
                    }
                }
            }
        }

    private fun requestLocationPermission(view: View): Boolean {

        if (checkPerms()) {
            Snackbar.make(view, " Permission  granted", Snackbar.LENGTH_SHORT).show()
            return true
        } else {
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
            return false
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

}

