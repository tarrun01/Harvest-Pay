package com.harvestpay.app.measurement

import com.harvestpay.app.localization.AppText
import com.harvestpay.app.R
import android.graphics.Color as AndroidColor
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.*
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.harvestpay.app.BuildConfig
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.delay
import kotlin.math.*

@Composable
fun BoundaryMap(
    points: List<GeoPoint>, current: GeoPoint? = null, satellite: Boolean = false,
    editable: Boolean = false, fit: Boolean = false,
    onAdd: (GeoPoint) -> Unit = {}, onMove: (Int,GeoPoint) -> Unit = { _,_ -> },
    onSelect: (Int) -> Unit = {}, modifier: Modifier = Modifier,
) {
    val context=LocalContext.current
    val connectivity=remember { context.getSystemService(ConnectivityManager::class.java) }
    fun hasInternet()=connectivity.getNetworkCapabilities(connectivity.activeNetwork)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)==true
    var online by remember { mutableStateOf(hasInternet()) }
    DisposableEffect(connectivity) {
        val callback=object: ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) { online=hasInternet() }
            override fun onLost(network: Network) { online=hasInternet() }
        }
        runCatching { connectivity.registerNetworkCallback(NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build(),callback) }
        onDispose { runCatching { connectivity.unregisterNetworkCallback(callback) } }
    }
    val canMap=BuildConfig.MAPS_CONFIGURED && GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)==ConnectionResult.SUCCESS
    var fallback by remember { mutableStateOf(!canMap) }
    var retry by remember { mutableIntStateOf(0) }
    var loaded by remember(retry) { mutableStateOf(false) }
    var timeout by remember(retry) { mutableStateOf(false) }
    LaunchedEffect(retry,canMap) { if(canMap) { delay(20000); if(!loaded) timeout=true } }
    Column(modifier) {
        if (!canMap || timeout || fallback || !online) {
            Text(if(!BuildConfig.MAPS_CONFIGURED) AppText.text(R.string.text_map_imagery_needs_a_configured_google_maps_key_gps_and) else if(!online) AppText.text(R.string.text_no_internet_for_map_imagery_your_boundary_is_retained_u) else AppText.text(R.string.text_map_imagery_unavailable_check_internet_and_google_play),style=MaterialTheme.typography.bodySmall,modifier=Modifier.padding(8.dp))
            if(canMap) Row {
                TextButton(onClick={ retry++; fallback=false }) { Text(AppText.text(R.string.text_retry_imagery)) }
                if(!fallback) TextButton(onClick={ fallback=true }) { Text(AppText.text(R.string.text_coordinate_view)) }
            }
        }
        if (fallback) CoordinateMap(points,current,editable,onMove,onSelect,Modifier.fillMaxWidth().weight(1f))
        else key(retry) {
            val mapView=remember { MapView(context).apply { onCreate(null) } }
            var googleMap by remember { mutableStateOf<GoogleMap?>(null) }
            val boundaryMarkers = remember { mutableListOf<Marker>() }
            var locationMarker by remember { mutableStateOf<Marker?>(null) }
            var boundaryLine by remember { mutableStateOf<Polyline?>(null) }
            var boundaryPolygon by remember { mutableStateOf<Polygon?>(null) }
            var centered by remember { mutableStateOf(false) }
            var movedByUser by remember { mutableStateOf(false) }
            val lifecycle=LocalLifecycleOwner.current.lifecycle
            val add by rememberUpdatedState(onAdd); val move by rememberUpdatedState(onMove); val select by rememberUpdatedState(onSelect)
            DisposableEffect(mapView,lifecycle) {
                val observer=LifecycleEventObserver { _,event -> when(event) {
                    Lifecycle.Event.ON_START -> mapView.onStart()
                    Lifecycle.Event.ON_RESUME -> mapView.onResume()
                    Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                    Lifecycle.Event.ON_STOP -> mapView.onStop()
                    else -> Unit
                } }
                lifecycle.addObserver(observer)
                if(lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) mapView.onStart()
                if(lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) mapView.onResume()
                mapView.getMapAsync { map ->
                    googleMap=map; map.mapType=if(satellite) GoogleMap.MAP_TYPE_SATELLITE else GoogleMap.MAP_TYPE_HYBRID
                    map.uiSettings.isZoomControlsEnabled=true
                    map.setOnMapLoadedCallback { loaded=true; timeout=false }
                    map.setOnCameraMoveStartedListener { if(it==GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) movedByUser=true }
                    map.setOnMapClickListener { add(GeoPoint(it.latitude,it.longitude)) }
                    map.setOnMarkerClickListener { marker -> (marker.tag as? Int)?.let(select); true }
                    map.setOnMarkerDragListener(object: GoogleMap.OnMarkerDragListener {
                        override fun onMarkerDragStart(marker: Marker) {}
                        override fun onMarkerDrag(marker: Marker) {}
                        override fun onMarkerDragEnd(marker: Marker) { (marker.tag as? Int)?.let { move(it,GeoPoint(marker.position.latitude,marker.position.longitude)) } }
                    })
                }
                onDispose { lifecycle.removeObserver(observer); mapView.onPause(); mapView.onStop(); mapView.onDestroy() }
            }
            AndroidView(factory={mapView},modifier=Modifier.fillMaxWidth().weight(1f))
            LaunchedEffect(googleMap,points,current,editable) {
                val map=googleMap ?: return@LaunchedEffect
                while(boundaryMarkers.size>points.size) boundaryMarkers.removeAt(boundaryMarkers.lastIndex).remove()
                points.forEachIndexed { i,p ->
                    val position=LatLng(p.latitude,p.longitude)
                    if(i>=boundaryMarkers.size) map.addMarker(MarkerOptions().position(position).title(AppText.text(R.string.text_boundary_point_1_s, i+1)).draggable(editable).icon(BitmapDescriptorFactory.defaultMarker(if(i==0) BitmapDescriptorFactory.HUE_GREEN else BitmapDescriptorFactory.HUE_ORANGE)))?.let { it.tag=i; boundaryMarkers.add(it) }
                    else { boundaryMarkers[i].position=position; boundaryMarkers[i].isDraggable=editable }
                }
                val positions=points.map { LatLng(it.latitude,it.longitude) }
                if(points.size>=2) {
                    if(boundaryLine==null) boundaryLine=map.addPolyline(PolylineOptions().addAll(positions).geodesic(true).color(AndroidColor.rgb(34,160,98)).width(5f))
                    else boundaryLine?.points=positions
                } else { boundaryLine?.remove(); boundaryLine=null }
                if(points.size>=3) {
                    if(boundaryPolygon==null) boundaryPolygon=map.addPolygon(PolygonOptions().addAll(positions).geodesic(true).strokeColor(AndroidColor.rgb(34,160,98)).fillColor(0x3322A062).strokeWidth(4f))
                    else boundaryPolygon?.points=positions
                } else { boundaryPolygon?.remove(); boundaryPolygon=null }
                current?.let {
                    val position=LatLng(it.latitude,it.longitude)
                    if(locationMarker==null) locationMarker=map.addMarker(MarkerOptions().position(position).title(AppText.text(R.string.text_current_location)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                    else locationMarker?.position=position
                }
                if(!centered && !movedByUser) {
                    if(fit && points.isNotEmpty()) {
                        val bounds=LatLngBounds.builder(); points.forEach { bounds.include(LatLng(it.latitude,it.longitude)) }
                        mapView.post { runCatching { map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(),70)) } }
                        centered=true
                    } else if(current!=null || points.isNotEmpty()) {
                        val p=current ?: points.first(); map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(p.latitude,p.longitude),18f)); centered=true
                    } else if(satellite) map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(22.0,79.0),4f))
                }
            }
        }
    }
}

/** Independent of imagery: local coordinate sketch with pinch zoom and pan. */
@Composable
private fun CoordinateMap(points: List<GeoPoint>,current: GeoPoint?,editable: Boolean,onMove: (Int,GeoPoint)->Unit,onSelect: (Int)->Unit,modifier: Modifier) {
    var zoom by remember { mutableFloatStateOf(1f) }; var pan by remember { mutableStateOf(Offset.Zero) }
    var viewportSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }
    val line=MaterialTheme.colorScheme.primary
    val all=points+listOfNotNull(current)
    // Freeze the initial camera so a user's pan is never undone by a GPS update.
    var initial by remember { mutableStateOf<List<GeoPoint>>(emptyList()) }
    LaunchedEffect(all.isNotEmpty()) { if(initial.isEmpty() && all.isNotEmpty()) initial=all }
    val latestPoints by rememberUpdatedState(points)
    val move by rememberUpdatedState(onMove); val select by rememberUpdatedState(onSelect)
    val origin=initial.firstOrNull()
    val cosLat=origin?.let { cos(Math.toRadians(it.latitude)).coerceAtLeast(.000001) } ?: 1.0
    fun xy(p: GeoPoint): Pair<Double,Double> {
        val o=origin ?: p
        var dl=p.longitude-o.longitude; if(dl>180) dl-=360; if(dl< -180) dl+=360
        return Pair(dl*cosLat,p.latitude-o.latitude)
    }
    val initialXY=initial.map(::xy)
    val minX=initialXY.minOfOrNull { it.first } ?: 0.0; val maxX=initialXY.maxOfOrNull { it.first } ?: 0.0
    val minY=initialXY.minOfOrNull { it.second } ?: 0.0; val maxY=initialXY.maxOfOrNull { it.second } ?: 0.0
    fun scale()=min(viewportSize.width/max(maxX-minX,.0003),viewportSize.height/max(maxY-minY,.0003))*.75*zoom
    fun at(p:GeoPoint): Offset {
        val v=xy(p); val s=scale()
        return Offset((viewportSize.width/2+(v.first-(minX+maxX)/2)*s).toFloat(),(viewportSize.height/2-(v.second-(minY+maxY)/2)*s).toFloat())+pan
    }
    Canvas(modifier.pointerInput(editable,origin,initial) {
        detectTransformGestures { center,delta,magnification,_ ->
            val index=if(editable && magnification==1f) latestPoints.indices.minByOrNull { (at(latestPoints[it])-center).getDistance() } else null
            if(index!=null && (at(latestPoints[index])-center).getDistance()<32.dp.toPx() && origin!=null && scale()>0) {
                val end=at(latestPoints[index])+delta
                val latitude=origin.latitude+(viewportSize.height/2-(end.y-pan.y))/scale()+(minY+maxY)/2
                var longitude=origin.longitude+((end.x-pan.x-viewportSize.width/2)/scale()+(minX+maxX)/2)/cosLat
                while(longitude>180) longitude-=360; while(longitude< -180) longitude+=360
                val p=GeoPoint(latitude,longitude)
                if(p.valid) { select(index); move(index,p) }
            } else { zoom=(zoom*magnification).coerceIn(.2f,30f); pan+=delta }
        }
    }) {
        viewportSize=size
        if(origin==null || all.isEmpty()) return@Canvas
        val path=Path(); points.forEachIndexed { i,p -> val v=at(p); if(i==0) path.moveTo(v.x,v.y) else path.lineTo(v.x,v.y) }
        if(points.size>=3) { path.close(); drawPath(path,line.copy(alpha=.15f)) }
        drawPath(path,line,style=Stroke(4f)); points.forEach { drawCircle(line,7f,at(it)) }
        if(current!=null) drawCircle(Color(0xFF2196F3),8f,at(current))
    }
}
