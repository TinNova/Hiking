package com.tinnovakovic.hiking.data.location

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.tinnovakovic.hiking.R
import com.tinnovakovic.hiking.shared.ApplicationCoroutineScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class LocationService : Service() {

    @Inject
    lateinit var locationClient: LocationClient

    @Inject
    lateinit var locationMemoryCache: LocationInMemoryCache

    @Inject
    lateinit var applicationCoroutineScope: ApplicationCoroutineScope

    private var locationJob: Job = Job()

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Hiking App")
            .setContentText("Is tracking your location")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true) //so you can't swipe it away

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        locationJob = locationClient
            .getLocationUpdates()
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                val updatedNotification = notification.setContentText(
                    "Location recently updated"
                )
                Log.d("TINTIN", "LocationService: location: $location")
                notificationManager.notify(LOCATION_NOTIFICATION_ID, updatedNotification.build())
                locationMemoryCache.updateCache(location)
            }
            .launchIn(applicationCoroutineScope.coroutineScope)


        startForeground(LOCATION_NOTIFICATION_ID, notification.build())

    }

    private fun stop() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        locationJob.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"

        const val LOCATION_NOTIFICATION_ID = 2
        const val NOTIFICATION_CHANNEL_ID = "hiking_location"
    }
}