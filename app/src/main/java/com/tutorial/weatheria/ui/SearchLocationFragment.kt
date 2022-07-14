package com.tutorial.weatheria.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.tutorial.weatheria.LocationAdapter
import com.tutorial.weatheria.Resource
import com.tutorial.weatheria.arch.WeatherViewModel
import com.tutorial.weatheria.databinding.FragmentSearchLocationBinding

class SearchLocationFragment : Fragment() {
    private var _binding: FragmentSearchLocationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WeatherViewModel by activityViewModels()

    // val adapter: ForecastAdapter by lazy { ForecastAdapter() }
    val adapter: LocationAdapter by lazy { LocationAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSearchLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    viewModel.updateSearchedLocation(query)
                    return true
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
        setUpUi()
        adapter.adapterClick {
            val navigate =
                SearchLocationFragmentDirections.actionSearchLocationFragmentToWeatherDetailsFragment(it)
            findNavController().navigate(navigate)
        }
    }

    private fun setUpUi() {
        binding.locationRv.adapter = adapter
        binding.errorText.isVisible = adapter.itemCount <=0
        viewModel.searchLocationResult.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Successful -> {
                    adapter.submitList(response.data)
                }
                is Resource.Failure -> {
                    binding.errorText.text = response.msg
                }
                else-> Unit
            }
        }


    }
}
