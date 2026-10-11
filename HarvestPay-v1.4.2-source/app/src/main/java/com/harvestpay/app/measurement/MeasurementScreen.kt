package com.harvestpay.app.measurement

import com.harvestpay.app.localization.AppText
import com.harvestpay.app.R
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.*
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import androidx.lifecycle.compose.*
import com.harvestpay.app.HarvestPayApplication
import com.harvestpay.app.data.AppSettings
import com.harvestpay.app.data.FieldEntity
import kotlinx.coroutines.*
import java.util.Locale

private fun Double.display() = MeasurementFormatting.display(this)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MeasurementScreen(settings: AppSettings, onAssign: () -> Unit, onClose: () -> Unit) {
    val context=LocalContext.current
    val app=context.applicationContext as HarvestPayApplication
    val store=app.measurementStore
    val draft by store.draft.collectAsStateWithLifecycle()
    val fix by store.fix.collectAsStateWithLifecycle()
    val gpsMessage by store.gpsMessage.collectAsStateWithLifecycle()
    val persistenceError by store.persistenceError.collectAsStateWithLifecycle()
    val scope=rememberCoroutineScope()
    var message by remember { mutableStateOf<String?>(null) }
    var confirmCancel by remember { mutableStateOf(false) }
    var conversion by remember { mutableStateOf(false) }
    var factor by remember(settings.bighaPerAcre) { mutableStateOf(settings.bighaPerAcre?.toString().orEmpty()) }
    var selected by remember { mutableStateOf<Int?>(null) }
    var pointEditor by remember { mutableStateOf(false) }
    var editingPoint by remember { mutableStateOf(false) }
    var latitude by remember { mutableStateOf("") }; var longitude by remember { mutableStateOf("") }
    var searching by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }
    var coordinateTarget by remember { mutableStateOf<GeoPoint?>(null) }
    var permissionTick by remember { mutableIntStateOf(0) }
    var locateSatellite by remember { mutableStateOf(false) }
    var now by remember { mutableLongStateOf(SystemClock.elapsedRealtime()) }
    LaunchedEffect(draft?.method,draft?.stage) { if(draft?.method==MeasurementMethod.GPS && draft?.stage==MeasurementStage.MEASURING) while(true) { delay(1000); now=SystemClock.elapsedRealtime() } }
    val fine=ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED
    val manager=remember { context.getSystemService(LocationManager::class.java) }
    val locationEnabled=manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    val permission=rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        permissionTick++
        if(result[Manifest.permission.ACCESS_FINE_LOCATION]!=true) message=AppText.text(R.string.text_precise_location_is_needed_for_gps_measurement_enable_p)
    }
    fun requestLocation() { permission.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)) }
    val lifecycle=LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val observer=LifecycleEventObserver { _,event -> if(event==Lifecycle.Event.ON_RESUME) permissionTick++ }
        lifecycle.addObserver(observer); onDispose { lifecycle.removeObserver(observer) }
    }
    LaunchedEffect(draft?.method) { if(draft?.method==MeasurementMethod.GPS && !fine) requestLocation() }
    // Only a foreground screen gets preview fixes. Active recording is exclusively
    // owned by the foreground service and continues when the screen is locked.
    DisposableEffect(fine,permissionTick,draft?.recording,draft?.stage,locateSatellite) {
        val listener=object: LocationListener {
            override fun onLocationChanged(location: Location) {
                store.fix.value=GpsSample(GeoPoint(location.latitude,location.longitude),location.accuracy.toDouble(),location.elapsedRealtimeNanos/1_000_000)
                if(locateSatellite) { coordinateTarget=GeoPoint(location.latitude,location.longitude); locateSatellite=false }
            }
            override fun onProviderDisabled(provider: String) {}
            override fun onProviderEnabled(provider: String) {}
            @Deprecated("Legacy Android callback") override fun onStatusChanged(provider: String?,status: Int,extras: Bundle?) {}
        }
        fun stop() { runCatching { manager.removeUpdates(listener) } }
        fun start() {
            if(fine && draft?.recording!=true && draft?.stage==MeasurementStage.MEASURING && (draft?.method==MeasurementMethod.GPS || locateSatellite)) {
                runCatching { manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2000L,0f,listener,Looper.getMainLooper()) }
                    .onFailure { message=AppText.text(R.string.text_gps_unavailable_check_device_location_settings) }
            }
        }
        val observer=LifecycleEventObserver { _,event -> when(event) { Lifecycle.Event.ON_RESUME -> start(); Lifecycle.Event.ON_PAUSE -> stop(); else -> Unit } }
        lifecycle.addObserver(observer)
        if(lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) start()
        onDispose { stop(); lifecycle.removeObserver(observer) }
    }
    fun stopService() { context.startService(Intent(context,GpsMeasurementService::class.java).setAction(GpsMeasurementService.STOP)); store.update { it.copy(recording=false) } }
    fun close() { if(draft?.points?.isNotEmpty()==true || draft?.recording==true) confirmCancel=true else { store.clear(); onClose() } }
    BackHandler { close() }
    val d=draft
    if(d==null) { Column(Modifier.padding(16.dp)) { Text(AppText.text(R.string.text_no_active_measurement)); Button(onClick=onClose) { Text(AppText.text(R.string.text_back)) } }; return }
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.padding(horizontal=16.dp,vertical=8.dp)) {
            Text(when(d.stage) { MeasurementStage.MEASURING -> if(d.method==MeasurementMethod.GPS) AppText.text(R.string.text_gps_field_measurement) else AppText.text(R.string.text_satellite_field_measurement); MeasurementStage.REVIEW -> AppText.text(R.string.text_review_field_boundary); MeasurementStage.ACCEPTED -> AppText.text(R.string.text_field_measurement_complete) },style=MaterialTheme.typography.titleLarge)
            Text(AppText.text(R.string.text_1_s_acres, d.acres.display()),style=MaterialTheme.typography.headlineSmall)
            if(d.method==MeasurementMethod.GPS && d.stage==MeasurementStage.MEASURING) {
                val fresh=fix?.let { now-it.elapsedMillis<15000 }==true
                val accuracy=fix?.accuracyMeters
                val signal=if(!fresh) AppText.text(R.string.text_waiting_signal_lost) else if(accuracy!=null && accuracy<=minOf(5.0,settings.gpsMaximumAccuracyMeters)) AppText.text(R.string.text_good) else if(accuracy!=null && accuracy<=settings.gpsMaximumAccuracyMeters) AppText.text(R.string.text_usable) else AppText.text(R.string.text_poor_points_are_filtered)
                Text(AppText.text(R.string.text_accuracy_1_s_2_s, accuracy?.let { AppText.text(R.string.text_1_s_m, it.display()) } ?: AppText.text(R.string.text_waiting), signal),color=if(!fresh || accuracy==null || accuracy>settings.gpsMaximumAccuracyMeters) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                if(d.recording || d.interrupted) Text(gpsMessage,style=MaterialTheme.typography.bodySmall)
                if(d.interrupted) Text(AppText.text(R.string.text_interrupted_measurement_restored_review_any_gap_in_the),color=MaterialTheme.colorScheme.error)
                if(!fine) Row { TextButton(onClick={requestLocation()}) { Text(AppText.text(R.string.text_allow_precise_location)) }; TextButton(onClick={ context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,android.net.Uri.parse("package:${context.packageName}"))) }) { Text(AppText.text(R.string.text_app_permissions)) } }
                if(!locationEnabled) TextButton(onClick={context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))}) { Text(AppText.text(R.string.text_enable_device_location)) }
            }
            Text(AppText.text(R.string.text_1_s_boundary_points_2_s_m, AppText.quantity(R.plurals.boundary_point_count, d.points.size, d.points.size), d.areaSquareMeters.display()),style=MaterialTheme.typography.bodySmall)
            persistenceError?.let { Text(AppText.label(it),color=MaterialTheme.colorScheme.error) }
            message?.let { Text(AppText.label(it),color=MaterialTheme.colorScheme.error) }
        }
        key(d.stage,coordinateTarget) {
            BoundaryMap(d.points,current=if(d.stage==MeasurementStage.MEASURING) coordinateTarget ?: (if(d.method==MeasurementMethod.GPS || locateSatellite) fix?.point else null) else null,
                satellite=d.method==MeasurementMethod.SATELLITE,editable=d.stage==MeasurementStage.REVIEW,fit=d.stage!=MeasurementStage.MEASURING,
                onAdd={ if(d.method==MeasurementMethod.SATELLITE && d.stage==MeasurementStage.MEASURING) store.edit(d.points+it) },
                onMove={ i,p -> if(d.stage==MeasurementStage.REVIEW) store.edit(d.points.toMutableList().apply { set(i,p) }) },
                onSelect={ selected=it },modifier=Modifier.fillMaxWidth().weight(1f))
        }
        if(d.stage==MeasurementStage.REVIEW) {
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceEvenly) {
                TextButton(onClick={ store.undo() },enabled=d.undo.isNotEmpty()) { Text(AppText.text(R.string.text_undo_edit)) }
                TextButton(onClick={ pointEditor=true }) { Text(AppText.text(R.string.text_edit_coordinates)) }
                TextButton(onClick={ selected?.let { i -> if(i in d.points.indices) store.edit(d.points.filterIndexed { n,_ -> n!=i }); selected=null } },enabled=selected!=null) { Text(AppText.text(R.string.text_remove_selected)) }
            }
        }
        Column(Modifier.padding(12.dp),verticalArrangement=Arrangement.spacedBy(4.dp)) {
            if(d.stage==MeasurementStage.MEASURING) {
                if(d.method==MeasurementMethod.SATELLITE) {
                    FlowRow(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick={store.undo()},enabled=d.undo.isNotEmpty()) { Text(AppText.text(R.string.text_undo)) }
                        TextButton(onClick={store.edit(emptyList())},enabled=d.points.isNotEmpty()) { Text(AppText.text(R.string.text_clear)) }
                        TextButton(onClick={locateSatellite=true; if(!fine) requestLocation() else if(!locationEnabled) message=AppText.text(R.string.text_enable_device_location_to_use_this_shortcut)}) { Text(AppText.text(R.string.text_my_location)) }
                        TextButton(onClick={searching=true}) { Text(AppText.text(R.string.text_search)) }
                    }
                }
                if(d.method==MeasurementMethod.GPS && d.recording) TextButton(onClick={stopService()}) { Text(AppText.text(R.string.text_pause_tracking)) }
                Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                    if(d.method==MeasurementMethod.GPS && !d.recording) Button(onClick={
                        if(!fine) requestLocation() else if(!locationEnabled) message=AppText.text(R.string.text_enable_device_location_first)
                        else if(fix==null || now-fix!!.elapsedMillis>15000 || fix!!.accuracyMeters>settings.gpsMaximumAccuracyMeters) message=AppText.text(R.string.text_wait_for_an_accurate_recent_gps_fix)
                        else { message=null; ContextCompat.startForegroundService(context,Intent(context,GpsMeasurementService::class.java)) }
                    },modifier=Modifier.weight(1f)) { Text(if(d.points.isEmpty()) AppText.text(R.string.text_start_measurement) else AppText.text(R.string.text_resume_measurement)) }
                    if(d.method==MeasurementMethod.SATELLITE || d.points.size>=3) Button(onClick={
                        if(d.method==MeasurementMethod.GPS) stopService()
                        val error=FieldGeometry.validationError(d.points)
                        if(error!=null && d.method==MeasurementMethod.SATELLITE) message=error else { message=null; store.update { it.copy(stage=MeasurementStage.REVIEW,recording=false) } }
                    },enabled=if(d.method==MeasurementMethod.GPS) d.points.size>=3 else FieldGeometry.validationError(d.points)==null,modifier=Modifier.weight(1f)) { Text(AppText.text(R.string.text_mark_complete)) }
                    OutlinedButton(onClick={close()}) { Text(AppText.text(R.string.text_cancel)) }
                }
            } else if(d.stage==MeasurementStage.REVIEW) {
                FieldGeometry.validationError(d.points)?.let { Text(AppText.label(it),color=MaterialTheme.colorScheme.error) }
                Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick={message=null; store.update { it.copy(stage=MeasurementStage.MEASURING) }},modifier=Modifier.weight(1f)) { Text(AppText.text(R.string.text_back_to_measurement)) }
                    Button(onClick={store.update { it.copy(stage=MeasurementStage.ACCEPTED,measuredAt=System.currentTimeMillis()) }},enabled=FieldGeometry.validationError(d.points)==null,modifier=Modifier.weight(1f)) { Text(AppText.text(R.string.text_finish)) }
                }
            } else {
                Text(AppText.text(R.string.text_method_1_s, AppText.label(d.method.name)))
                (d.factorUsed ?: settings.bighaPerAcre)?.let { Text(AppText.text(R.string.text_1_s_bigha, FieldGeometry.bigha(d.areaSquareMeters, it).display())) }
                Text(AppText.text(R.string.text_this_measurement_has_not_been_saved_as_a_field),style=MaterialTheme.typography.bodySmall)
                Button(onClick={
                    val configured=settings.bighaPerAcre
                    if(configured==null) conversion=true else { store.update { it.copy(factorUsed=configured) }; onAssign() }
                },modifier=Modifier.fillMaxWidth()) { Text(AppText.text(R.string.text_assign_to_customer)) }
                TextButton(onClick={store.update { it.copy(stage=MeasurementStage.REVIEW) }}) { Text(AppText.text(R.string.text_review_boundary_again)) }
            }
            Text(AppText.text(R.string.text_smartphone_gps_and_satellite_imagery_are_estimates_not),style=MaterialTheme.typography.labelSmall)
        }
    }
    if(confirmCancel) AlertDialog(onDismissRequest={confirmCancel=false},title={Text(AppText.text(R.string.text_discard_measurement))},text={Text(AppText.text(R.string.text_this_removes_the_current_boundary_draft))},confirmButton={TextButton(onClick={stopService(); store.clear(); confirmCancel=false; onClose()}) { Text(AppText.text(R.string.text_discard)) }},dismissButton={TextButton(onClick={confirmCancel=false}) { Text(AppText.text(R.string.text_keep_measuring)) }})
    if(conversion) AlertDialog(onDismissRequest={conversion=false},title={Text(AppText.text(R.string.text_acre_to_bigha_conversion))},text={Column { Text(AppText.text(R.string.text_enter_your_local_conversion_no_regional_value_is_assume)); OutlinedTextField(factor,{factor=it},label={Text(AppText.text(R.string.text_1_acre_bigha))},singleLine=true); message?.let { Text(AppText.label(it),color=MaterialTheme.colorScheme.error) } }},confirmButton={TextButton(onClick={
        val value=factor.toDoubleOrNull()
        if(value==null || !value.isFinite() || value<=0) message=AppText.text(R.string.text_enter_a_positive_decimal_conversion_factor)
        else scope.launch { try { app.settingsRepository.saveLandSettings(value,settings.gpsMaximumAccuracyMeters); store.update { it.copy(factorUsed=value) }; conversion=false; onAssign() } catch (_: Exception) { message=AppText.text(R.string.text_could_not_save_the_conversion_try_again) } }
    }) { Text(AppText.text(R.string.text_save_and_assign)) }},dismissButton={TextButton(onClick={conversion=false}) { Text(AppText.text(R.string.text_cancel)) }})
    if(pointEditor) AlertDialog(onDismissRequest={pointEditor=false},title={Text(AppText.text(R.string.text_boundary_coordinates))},text={LazyColumn(Modifier.heightIn(max=300.dp)) {
        itemsIndexed(d.points) { i,p -> TextButton(onClick={selected=i; latitude=p.latitude.toString(); longitude=p.longitude.toString(); pointEditor=false; editingPoint=true}) { Text(AppText.text(R.string.text_1_s_2_s_3_s_350f51, i+1, p.latitude, p.longitude)) } }
    }},confirmButton={TextButton(onClick={pointEditor=false}) { Text(AppText.text(R.string.text_close)) }})
    if(editingPoint && selected!=null) AlertDialog(onDismissRequest={editingPoint=false},title={Text(AppText.text(R.string.text_edit_point_1_s, selected!!+1))},text={Column { OutlinedTextField(latitude,{latitude=it},label={Text(AppText.text(R.string.text_latitude))}); OutlinedTextField(longitude,{longitude=it},label={Text(AppText.text(R.string.text_longitude))}); message?.let { Text(AppText.label(it),color=MaterialTheme.colorScheme.error) } }},confirmButton={TextButton(onClick={
        val lat=latitude.toDoubleOrNull(); val lon=longitude.toDoubleOrNull(); val i=selected!!
        if(lat!=null && lon!=null && GeoPoint(lat,lon).valid && i in d.points.indices) { store.edit(d.points.toMutableList().apply { set(i,GeoPoint(lat,lon)) }); editingPoint=false } else message=AppText.text(R.string.text_enter_valid_latitude_and_longitude)
    }) { Text(AppText.text(R.string.text_apply)) }},dismissButton={TextButton(onClick={val i=selected!!; store.edit(d.points.filterIndexed { n,_ -> n!=i }); editingPoint=false; selected=null}) { Text(AppText.text(R.string.text_remove_point)) }})
    if(searching) AlertDialog(onDismissRequest={searching=false},title={Text(AppText.text(R.string.text_find_location))},text={Column { OutlinedTextField(search,{search=it},label={Text(AppText.text(R.string.text_place_name_or_latitude_longitude))}); Text(AppText.text(R.string.text_place_search_needs_internet_coordinates_work_directly),style=MaterialTheme.typography.bodySmall) }},confirmButton={TextButton(onClick={scope.launch {
        val parts=search.split(','); val lat=parts.getOrNull(0)?.trim()?.toDoubleOrNull(); val lon=parts.getOrNull(1)?.trim()?.toDoubleOrNull()
        val p=if(lat!=null && lon!=null) GeoPoint(lat,lon).takeIf { it.valid } else withContext(Dispatchers.IO) { runCatching { @Suppress("DEPRECATION") val result=Geocoder(context).getFromLocationName(search,1)?.firstOrNull(); result?.let { GeoPoint(it.latitude,it.longitude) } }.getOrNull() }
        if(p==null) message=AppText.text(R.string.text_location_not_found_try_coordinates_or_another_place_nam) else { coordinateTarget=p; searching=false; message=null }
    }}) { Text(AppText.text(R.string.text_find)) }},dismissButton={TextButton(onClick={searching=false}) { Text(AppText.text(R.string.text_cancel)) }})
}

@Composable
fun LandMeasurementSettings(settings: AppSettings) {
    val app=LocalContext.current.applicationContext as HarvestPayApplication
    val scope=rememberCoroutineScope()
    var factor by remember(settings.bighaPerAcre) { mutableStateOf(settings.bighaPerAcre?.toString().orEmpty()) }
    var accuracy by remember(settings.gpsMaximumAccuracyMeters) { mutableStateOf(settings.gpsMaximumAccuracyMeters.toString()) }
    var status by remember { mutableStateOf("") }
    Column(Modifier.padding(16.dp).verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(16.dp)) {
        Text(AppText.text(R.string.text_land_measurement_settings),style=MaterialTheme.typography.titleLarge)
        OutlinedTextField(factor,{factor=it},label={Text(AppText.text(R.string.text_1_acre_bigha))},supportingText={Text(AppText.text(R.string.text_enter_your_local_positive_conversion_factor))},singleLine=true,modifier=Modifier.fillMaxWidth())
        OutlinedTextField(accuracy,{accuracy=it},label={Text(AppText.text(R.string.text_maximum_gps_accuracy_error_m))},supportingText={Text(AppText.text(R.string.text_3_50_meters_lower_values_reject_more_noisy_points))},singleLine=true,modifier=Modifier.fillMaxWidth())
        Text(AppText.text(R.string.text_changing_this_conversion_does_not_alter_saved_field_siz))
        Button(onClick={ val f=factor.toDoubleOrNull(); val a=accuracy.toDoubleOrNull()
            if(f==null || !f.isFinite() || f<=0 || a==null || !a.isFinite() || a !in 3.0..50.0) status=AppText.text(R.string.text_enter_a_positive_factor_and_an_accuracy_threshold_from)
            else scope.launch { try { app.settingsRepository.saveLandSettings(f,a); status=AppText.text(R.string.text_land_measurement_settings_saved) } catch (_: Exception) { status=AppText.text(R.string.text_could_not_save_settings_try_again) } }
        },modifier=Modifier.fillMaxWidth()) { Text(AppText.text(R.string.text_save)) }
        Text(status)
        TextButton(onClick={ app.startActivity(Intent(Intent.ACTION_VIEW,android.net.Uri.parse("https://policies.google.com/privacy")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }) { Text(AppText.text(R.string.text_google_map_provider_privacy_policy)) }
        TextButton(onClick={ app.startActivity(Intent(Intent.ACTION_VIEW,android.net.Uri.parse("https://maps.google.com/help/terms_maps.html")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }) { Text(AppText.text(R.string.text_google_maps_terms)) }
    }
}

@Composable
fun SavedBoundaryScreen(field: FieldEntity?) {
    if(field==null) { Text(AppText.text(R.string.text_field_unavailable),Modifier.padding(16.dp)); return }
    val points=remember(field.boundaryCoordinates) { decodeBoundary(field.boundaryCoordinates) }
    Column(Modifier.fillMaxSize()) {
        Text(field.fieldName,style=MaterialTheme.typography.titleLarge,modifier=Modifier.padding(16.dp))
        Text(AppText.text(R.string.text_1_s_2_s_acres_3_s_bigha, AppText.label(field.measurementMethod), field.measuredAreaAcres?.display().orEmpty(), field.sizeBigha.display()),modifier=Modifier.padding(horizontal=16.dp))
        BoundaryMap(points,satellite=field.measurementMethod=="SATELLITE",fit=true,modifier=Modifier.weight(1f).fillMaxWidth())
    }
}
