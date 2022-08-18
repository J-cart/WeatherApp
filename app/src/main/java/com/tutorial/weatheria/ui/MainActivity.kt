package com.tutorial.weatheria.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.location.*
import com.google.firebase.firestore.FirebaseFirestore
import com.tutorial.weatheria.R
import com.tutorial.weatheria.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.tasks.await

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.currentWeatherFragment,
                R.id.searchLocationFragment,
                R.id.savedWeatherFragment,
                R.id.forecastWeatherDetailFragment
            )
        )

        val fragHost = supportFragmentManager.findFragmentById(R.id.fragHost) as NavHostFragment
        navController = fragHost.findNavController()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNav.isVisible =
                appBarConfiguration.topLevelDestinations.contains(destination.id)
        }

        setupActionBarWithNavController(navController,appBarConfiguration)
        binding.bottomNav.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}