package com.pratthamarora.fit_tastic.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.pratthamarora.fit_tastic.R
import com.pratthamarora.fit_tastic.ui.viewmodel.StatsViewModel
import com.pratthamarora.fit_tastic.utils.Utility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_statistics.*
import kotlin.math.round

@AndroidEntryPoint
class StatsFragment : Fragment(R.layout.fragment_statistics) {
    private val viewModel: StatsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.totalTimeRun.observe(viewLifecycleOwner, Observer {
            it?.let {
                val totalTime = Utility.getFormattedStopWatchTime(it)
                tvTotalTime.text = totalTime
            }
        })

        viewModel.totalDistance.observe(viewLifecycleOwner, Observer {
            it?.let {
                val distance = it / 1000f
                val totalDistance = round(distance * 10f) / 10f
                tvTotalDistance.text = "$totalDistance km"
            }
        })
        viewModel.totalSpeed.observe(viewLifecycleOwner, Observer {
            it?.let {
                val totalSpeed = round(it * 10f) / 10f
                tvTotalDistance.text = "$totalSpeed km/h"
            }
        })
        viewModel.totalCalories.observe(viewLifecycleOwner, Observer {
            it?.let {
                tvTotalCalories.text = "$it kcal"
            }
        })
    }
}