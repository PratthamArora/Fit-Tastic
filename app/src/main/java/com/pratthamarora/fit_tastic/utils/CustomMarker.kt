package com.pratthamarora.fit_tastic.utils

import android.content.Context
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.pratthamarora.fit_tastic.db.Run
import kotlinx.android.synthetic.main.marker_view.view.*
import java.text.SimpleDateFormat
import java.util.*

class CustomMarker(
    val runs: List<Run>,
    context: Context,
    layout: Int
) : MarkerView(context, layout) {

    override fun getOffset(): MPPointF {
        return MPPointF(-width / 2f, -height.toFloat())
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        if (e == null) {
            return
        }
        val currentRun = e.x.toInt()
        val run = runs[currentRun]

        val calender = Calendar.getInstance().apply {
            timeInMillis = run.timeStamp
        }
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        tvDate.text = dateFormat.format(calender.time)
        tvAvgSpeed.text = "${run.avgSpeed} km/h"
        tvDistance.text = "${run.distance / 1000f} km"
        tvDuration.text = Utility.getFormattedStopWatchTime(run.timeInMs)
        tvCaloriesBurned.text = "${run.calories} kcal"
    }
}