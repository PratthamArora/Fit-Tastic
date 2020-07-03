package com.pratthamarora.fit_tastic.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.pratthamarora.fit_tastic.R
import com.pratthamarora.fit_tastic.ui.MainActivity
import com.pratthamarora.fit_tastic.utils.Constants.ACTION_PAUSE_SERVICE
import com.pratthamarora.fit_tastic.utils.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.pratthamarora.fit_tastic.utils.Constants.ACTION_START_RESUME_SERVICE
import com.pratthamarora.fit_tastic.utils.Constants.ACTION_STOP_SERVICE
import com.pratthamarora.fit_tastic.utils.Constants.FASTEST_LOCATION_UPDATE_INTERVAL
import com.pratthamarora.fit_tastic.utils.Constants.LOCATION_UPDATE_INTERVAL
import com.pratthamarora.fit_tastic.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.pratthamarora.fit_tastic.utils.Constants.NOTIFICATION_CHANNEL_NAME
import com.pratthamarora.fit_tastic.utils.Constants.NOTIFICATION_ID
import com.pratthamarora.fit_tastic.utils.Utility
import timber.log.Timber

class TrackingService : LifecycleService() {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        val isTracking = MutableLiveData<Boolean>()
        val pathCoordinates = MutableLiveData<MutableList<MutableList<LatLng>>>()
    }

    var isFirstRun = true

    private fun postInitialValue() {
        isTracking.postValue(false)
        pathCoordinates.postValue(mutableListOf())
    }

    override fun onCreate() {
        super.onCreate()
        postInitialValue()
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        isTracking.observe(this, Observer {
            updateTracking(it)
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_RESUME_SERVICE -> {
                    if (isFirstRun) {
                        startForegroundService()
                        isFirstRun = false
                    } else {
                        Timber.d("not a first run")
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("paused service")
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d("stopped service")
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("MissingPermission")
    private fun updateTracking(isTracking: Boolean) {
        if (isTracking) {
            if (Utility.hasLocationPermission(this)) {
                val request = LocationRequest().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = FASTEST_LOCATION_UPDATE_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            } else {
                fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            }
        }
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            if (isTracking.value!!) {
                result?.locations?.let {
                    for (location in it) {
                        addPathPoints(location)
                        Timber.d("new location : ${location.latitude}, ${location.longitude}")
                    }
                }
            }
        }
    }

    private fun addPathPoints(location: Location?) {
        location?.let {
            val pos = LatLng(it.latitude, it.longitude)
            pathCoordinates.value?.apply {
                last().add(pos)
                pathCoordinates.postValue(this)
            }

        }
    }

    private fun addEmptyPath() = pathCoordinates.value?.apply {
        add(mutableListOf())
        pathCoordinates.postValue(this)
    } ?: pathCoordinates.postValue(mutableListOf(mutableListOf()))

    private fun getActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action = ACTION_SHOW_TRACKING_FRAGMENT
        },
        FLAG_UPDATE_CURRENT
    )

    private fun startForegroundService() {
        addEmptyPath()
        isTracking.postValue(true)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_directions_run_black_24dp)
            .setContentTitle("Fit-Tastic")
            .setContentText("00:00:00")
            .setContentIntent(getActivityPendingIntent())

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
}