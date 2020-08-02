package com.pratthamarora.fit_tastic.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import com.pratthamarora.fit_tastic.R
import com.pratthamarora.fit_tastic.db.Run
import com.pratthamarora.fit_tastic.services.Polyline
import com.pratthamarora.fit_tastic.services.TrackingService
import com.pratthamarora.fit_tastic.ui.viewmodel.MainViewModel
import com.pratthamarora.fit_tastic.utils.Constants.ACTION_PAUSE_SERVICE
import com.pratthamarora.fit_tastic.utils.Constants.ACTION_START_RESUME_SERVICE
import com.pratthamarora.fit_tastic.utils.Constants.ACTION_STOP_SERVICE
import com.pratthamarora.fit_tastic.utils.Constants.MAP_ZOOM
import com.pratthamarora.fit_tastic.utils.Constants.POLYLINE_COLOR
import com.pratthamarora.fit_tastic.utils.Constants.POLYLINE_WIDTH
import com.pratthamarora.fit_tastic.utils.Utility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tracking.*
import java.util.*
import javax.inject.Inject
import kotlin.math.round

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {

    @set:Inject
    var weight = 70f
    private val viewModel: MainViewModel by viewModels()

    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()

    private var map: GoogleMap? = null
    private var curTimeInMillis = 0L
    private var menu: Menu? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.tracking_menu, menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (curTimeInMillis > 0L) {
            this.menu?.getItem(0)?.isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cancelTracking -> {
                showCancelAlertDialog()
            }
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)

        btnToggleRun.setOnClickListener {
            toggleRun()
        }

        if (savedInstanceState != null) {
            val cancelTrackingAlertDialog =
                parentFragmentManager.findFragmentByTag("CancelTrackingAlertDialog")
                        as CancelTrackingAlertDialog?
            cancelTrackingAlertDialog?.setYesListener { stopRun() }
        }
        btnFinishRun.setOnClickListener {
            zoomTrack()
            endRunAndSave()
        }
        mapView.getMapAsync {
            map = it
            addAllPolylines()
            subscribeToObservers()
        }
    }


    private fun showCancelAlertDialog() {
        CancelTrackingAlertDialog().apply {
            setYesListener {
                stopRun()
            }
        }.show(parentFragmentManager, "CancelTrackingAlertDialog")
    }

    private fun stopRun() {
        tvTimer.text = "00:00:00:00"
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }


    private fun subscribeToObservers() {
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })

        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        })

        TrackingService.timeRunInMillis.observe(viewLifecycleOwner, Observer {
            curTimeInMillis = it
            val formattedTime = Utility.getFormattedStopWatchTime(curTimeInMillis, true)
            tvTimer.text = formattedTime
        })
    }

    private fun toggleRun() {
        when {
            isTracking -> {
                menu?.getItem(0)?.isVisible = true
                sendCommandToService(ACTION_PAUSE_SERVICE)
            }
            else -> {
                sendCommandToService(ACTION_START_RESUME_SERVICE)
            }
        }
    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        when {
            !isTracking && curTimeInMillis > 0L -> {
                btnToggleRun.text = "Start"
                btnFinishRun.isVisible = true
            }
            isTracking -> {
                btnToggleRun.text = "Stop"
                menu?.getItem(0)?.isVisible = true
                btnFinishRun.isGone = true
            }
        }
    }

    private fun moveCameraToUser() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    private fun zoomTrack() {
        val bounds = LatLngBounds.Builder()
        for (polyline in pathPoints) {
            for (pos in polyline) {
                bounds.include(pos)
            }
        }
        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                mapView.width,
                mapView.height,
                (mapView.height * 0.05f).toInt()
            )
        )
    }

    private fun endRunAndSave() {
        map?.snapshot {
            var distanceMeters = 0
            for (polyline in pathPoints) {
                distanceMeters += Utility.calculatePolylineLength(polyline).toInt()
            }
            val avgSpeed =
                round((distanceMeters / 1000f) / (curTimeInMillis / 1000f / 60 / 60) * 10) / 10f
            val dateTime = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceMeters / 1000f) * weight).toInt()
            val run = Run(
                it,
                dateTime,
                avgSpeed,
                distanceMeters,
                curTimeInMillis,
                caloriesBurned
            )
            viewModel.insertRun(run)
            Snackbar.make(
                requireActivity().findViewById(R.id.rootView),
                "Current Run Saved!",
                Snackbar.LENGTH_LONG
            ).show()
            stopRun()
        }
    }

    private fun addAllPolylines() {
        for (polyline in pathPoints) {
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolyline() {
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }


    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }
}