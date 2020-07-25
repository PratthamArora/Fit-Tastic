package com.pratthamarora.fit_tastic.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.pratthamarora.fit_tastic.R
import com.pratthamarora.fit_tastic.ui.viewmodel.StatsViewModel
import com.pratthamarora.fit_tastic.utils.CustomMarker
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
        setupChart()
    }

    private fun setupChart() {
        barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }
        barChart.axisLeft.apply {
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }
        barChart.axisRight.apply {
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }
        barChart.apply {
            description.text = "Avg Speed"
            legend.isEnabled = false
        }
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
                tvAverageSpeed.text = "$totalSpeed km/h"
            }
        })
        viewModel.totalCalories.observe(viewLifecycleOwner, Observer {
            it?.let {
                tvTotalCalories.text = "$it kcal"
            }
        })

        viewModel.sortedByDate.observe(viewLifecycleOwner, Observer {
            it?.let {
                val allSpeeds = it.indices.map { i ->
                    BarEntry(i.toFloat(), it[i].avgSpeed)
                }
                val dataSet = BarDataSet(allSpeeds, "Avg Speed").apply {
                    valueTextColor = Color.WHITE
                    color = ContextCompat.getColor(requireContext(), R.color.colorAccent)
                }
                barChart.data = BarData(dataSet)
                barChart.marker =
                    CustomMarker(it.reversed(), requireContext(), R.layout.marker_view)
                barChart.invalidate()
            }
        })
    }
}