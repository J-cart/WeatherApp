package com.tutorial.weatheria.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.tutorial.weatheria.arch.WeatherViewModel
import com.tutorial.weatheria.databinding.FragmentForecastWeatherDetailBinding

class ForecastWeatherDetailFragment : Fragment() {
    private var _binding: FragmentForecastWeatherDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WeatherViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentForecastWeatherDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val list = listOf<FragmentListWrapper>(
            FragmentListWrapper(DailyWeatherDetailsFragment(),"Daily"),
            FragmentListWrapper(WeeklyWeatherDetailsFragment(),"Weekly")
        )

        val tabMediator = TabLayoutMediator(binding.tabLayout,binding.viewPager){tab,position->
            tab.text = list[position].title
        }
        binding.viewPager.adapter = ViewPagingAdapter(this,list)
        tabMediator.attach()
    }

    class ViewPagingAdapter(fragment: Fragment, private val list:List<FragmentListWrapper>) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int {
            return list.size
        }

        override fun createFragment(position: Int): Fragment {

            return list[position].fragment
        /*    return when (position) {
                0 -> list[position].fragment
                1 -> list[position].fragment
                else -> throw  IllegalStateException("INVALID POSITION $position")
            }*/
        }
    }
    data class FragmentListWrapper(val fragment: Fragment,val title:String)
}


