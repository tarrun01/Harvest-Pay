package com.harvestpay.app.measurement

import com.harvestpay.app.localization.AppText
import com.harvestpay.app.R
import android.Manifest
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.location.*
import android.os.*
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.harvestpay.app.HarvestPayApplication
import com.harvestpay.app.MainActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

/** Native GNSS works offline and on devices without Google Play location services. */
class GpsMeasurementService : Service(), LocationListener {
    private val store get() = (application as HarvestPayApplication).measurementStore
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private lateinit var manager: LocationManager
    private var filter = GpsPointFilter()
    private var lastReceived = 0L
    private var startedAt = 0L
    private var accuracyThreshold = 15.0
    override fun onBind(intent: Intent?) = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == STOP) { stopTracking(); return START_NOT_STICKY }
        if (store.serviceRunning) return START_NOT_STICKY
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            store.gpsMessage.value=AppText.text(R.string.text_precise_location_permission_is_required); stopSelf(); return START_NOT_STICKY
        }
        manager = getSystemService(LocationManager::class.java)
        val notificationManager = getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= 26) notificationManager.createNotificationChannel(NotificationChannel("field_gps",AppText.text(R.string.text_field_measurement),NotificationManager.IMPORTANCE_LOW))
        val open = PendingIntent.getActivity(this,0,Intent(this,MainActivity::class.java),PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val pause = PendingIntent.getService(this,1,Intent(this,GpsMeasurementService::class.java).setAction(STOP),PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(this,"field_gps").setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(AppText.text(R.string.text_gps_field_measurement_active)).setContentText(AppText.text(R.string.text_recording_field_boundary_tap_to_return_to_harvest_pay))
            .setContentIntent(open).setOngoing(true).addAction(0,AppText.text(R.string.text_pause),pause).build()
        try {
            ServiceCompat.startForeground(this,1401,notification,if(Build.VERSION.SDK_INT>=29) ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION else 0)
            store.serviceRunning=true
            scope.launch {
                val settings=(application as HarvestPayApplication).settingsRepository.settings.first()
                accuracyThreshold=settings.gpsMaximumAccuracyMeters
                filter=GpsPointFilter(GpsFilterConfig(maximumAccuracyMeters=accuracyThreshold),store.draft.value?.points?.lastOrNull())
                store.update { it.copy(recording=true,interrupted=false) }
                lastReceived=SystemClock.elapsedRealtime(); startedAt=lastReceived
                try { manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2000L,0f,this@GpsMeasurementService,Looper.getMainLooper()) }
                catch (_: Exception) { store.gpsMessage.value=AppText.text(R.string.text_gps_is_unavailable_enable_device_location_and_resume); stopTracking() }
                while (store.serviceRunning) {
                    delay(3000)
                    if (SystemClock.elapsedRealtime()-lastReceived>30_000 || SystemClock.elapsedRealtime()-(filter.lastReliableMillis ?: startedAt)>30_000) {
                        store.gpsMessage.value=AppText.text(R.string.text_gps_signal_lost_tracking_paused_inspect_the_gap_before)
                        store.update { it.copy(interrupted=true) }; stopTracking()
                    }
                }
            }
        } catch (_: Exception) { store.gpsMessage.value=AppText.text(R.string.text_could_not_start_gps_tracking_open_the_app_and_try_again); stopTracking() }
        return START_NOT_STICKY
    }
    override fun onLocationChanged(location: Location) {
        lastReceived=SystemClock.elapsedRealtime()
        val sample=GpsSample(GeoPoint(location.latitude,location.longitude),location.accuracy.toDouble(),location.elapsedRealtimeNanos/1_000_000)
        store.fix.value=sample
        val resumePoint=store.draft.value?.points?.lastOrNull()
        if(filter.previous==null && resumePoint!=null && sample.accuracyMeters<=accuracyThreshold && FieldGeometry.distance(resumePoint,sample.point)>30.0) {
            store.gpsMessage.value=AppText.text(R.string.text_resume_within_30_meters_of_the_last_boundary_point_or_r)
            store.update { it.copy(interrupted=true) }; stopTracking(); return
        }
        if (filter.accept(sample)) {
            store.update { draft ->
                // Resume does not add a duplicate start point; old points remain editable.
                if (draft.points.lastOrNull()?.let { FieldGeometry.distance(it,sample.point)<2 } == true) draft
                else draft.copy(points=draft.points+sample.point,originalTrack=draft.originalTrack+sample.point)
            }
            store.gpsMessage.value=AppText.text(R.string.text_recording_valid_gps_points)
        } else store.gpsMessage.value=AppText.text(R.string.text_no_point_added_weak_accuracy_duplicate_point_or_implaus)
        val reliable=filter.lastReliableMillis
        if (reliable!=null && sample.elapsedMillis-reliable>30_000) {
            store.gpsMessage.value=AppText.text(R.string.text_no_reliable_points_for_30_seconds_tracking_paused_resum)
            store.update { it.copy(interrupted=true) }; stopTracking()
        }
    }
    override fun onProviderDisabled(provider: String) { store.gpsMessage.value=AppText.text(R.string.text_location_is_disabled_tracking_paused); store.update { it.copy(interrupted=true) }; stopTracking() }
    @Deprecated("Legacy Android callback") override fun onStatusChanged(provider: String?,status: Int,extras: Bundle?) {}
    override fun onProviderEnabled(provider: String) {}
    private fun stopTracking() {
        if (::manager.isInitialized) runCatching { manager.removeUpdates(this) }
        store.serviceRunning=false; store.update { it.copy(recording=false) }
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE); stopSelf()
    }
    override fun onDestroy() { if(::manager.isInitialized) runCatching { manager.removeUpdates(this) }; store.serviceRunning=false; store.update { it.copy(recording=false) }; scope.cancel(); super.onDestroy() }
    companion object { const val STOP="pause_field_measurement" }
}
