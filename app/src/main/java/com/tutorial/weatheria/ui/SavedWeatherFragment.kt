package com.tutorial.weatheria.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.tutorial.weatheria.databinding.FragmentSavedWeatherBinding

@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.N)
class SavedWeatherFragment : Fragment() {
    private var _binding: FragmentSavedWeatherBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSavedWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

}