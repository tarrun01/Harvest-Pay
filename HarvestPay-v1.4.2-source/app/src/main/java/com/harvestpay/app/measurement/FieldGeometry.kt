package com.harvestpay.app.measurement

import kotlin.math.*

data class GeoPoint(val latitude: Double, val longitude: Double) {
    val valid: Boolean get() = latitude.isFinite() && longitude.isFinite() && latitude in -90.0..90.0 && longitude in -180.0..180.0
}

/** Spherical excess of geodesic triangles, using the IUGG mean Earth radius. */
object FieldGeometry {
    const val EARTH_RADIUS = 6371009.0
    const val SQUARE_METERS_PER_ACRE = 4046.8564224
    fun acres(squareMeters: Double) = squareMeters / SQUARE_METERS_PER_ACRE
    fun hectares(squareMeters: Double) = squareMeters / 10000.0
    fun bigha(squareMeters: Double, factor: Double): Double {
        require(factor.isFinite() && factor > 0) { "Enter a positive conversion factor." }
        return acres(squareMeters) * factor
    }
    private fun vector(p: GeoPoint): DoubleArray {
        val lat = Math.toRadians(p.latitude); val lon = Math.toRadians(p.longitude)
        return doubleArrayOf(cos(lat) * cos(lon), cos(lat) * sin(lon), sin(lat))
    }
    private fun dot(a: DoubleArray, b: DoubleArray) = a.indices.sumOf { a[it] * b[it] }
    private fun cross(a: DoubleArray, b: DoubleArray) = doubleArrayOf(a[1]*b[2]-a[2]*b[1], a[2]*b[0]-a[0]*b[2], a[0]*b[1]-a[1]*b[0])
    fun area(points: List<GeoPoint>): Double {
        if (points.size < 3 || points.any { !it.valid }) return 0.0
        val first = vector(points.first())
        var excess = 0.0
        for (i in 1 until points.lastIndex) {
            val b = vector(points[i]); val c = vector(points[i + 1])
            excess += 2 * atan2(dot(first, cross(b, c)), 1 + dot(first,b) + dot(b,c) + dot(c,first))
        }
        return abs(excess) * EARTH_RADIUS * EARTH_RADIUS
    }
    fun distance(a: GeoPoint, b: GeoPoint): Double {
        val dLat = Math.toRadians(b.latitude-a.latitude)
        val dLon = Math.toRadians(b.longitude-a.longitude)
        val h = sin(dLat/2).pow(2) + cos(Math.toRadians(a.latitude))*cos(Math.toRadians(b.latitude))*sin(dLon/2).pow(2)
        return 2 * EARTH_RADIUS * asin(sqrt(h.coerceIn(0.0,1.0)))
    }
    // Gnomonic projection maps great circle edges to straight lines. Agricultural
    // boundaries must fit one hemisphere; this also handles the date line.
    fun validationError(points: List<GeoPoint>): String? {
        if (points.any { !it.valid }) return "Invalid geographic coordinates."
        if (points.size < 3) return "Select at least three boundary points."
        if (points.indices.any { distance(points[it], points[(it+1)%points.size]) < 0.05 }) return "Remove duplicate adjacent points."
        val lat0 = Math.toRadians(points.first().latitude); val lon0 = Math.toRadians(points.first().longitude)
        val xy = points.map {
            val lat = Math.toRadians(it.latitude); val lon = Math.toRadians(it.longitude)-lon0
            val denominator = sin(lat0)*sin(lat)+cos(lat0)*cos(lat)*cos(lon)
            if (denominator <= 0.001) return "Boundary is too large for a field."
            Pair(cos(lat)*sin(lon)/denominator, (cos(lat0)*sin(lat)-sin(lat0)*cos(lat)*cos(lon))/denominator)
        }
        fun orient(a: Pair<Double,Double>, b: Pair<Double,Double>, c: Pair<Double,Double>) = (b.first-a.first)*(c.second-a.second)-(b.second-a.second)*(c.first-a.first)
        fun onSegment(a: Pair<Double,Double>, b: Pair<Double,Double>, p: Pair<Double,Double>) = abs(orient(a,b,p)) < 1e-16 && p.first in min(a.first,b.first)-1e-12..max(a.first,b.first)+1e-12 && p.second in min(a.second,b.second)-1e-12..max(a.second,b.second)+1e-12
        for (i in points.indices) for (j in i+1 until points.size) {
            if (j == i+1 || i == 0 && j == points.lastIndex) continue
            val a=xy[i]; val b=xy[(i+1)%xy.size]; val c=xy[j]; val d=xy[(j+1)%xy.size]
            val crosses = orient(a,b,c)*orient(a,b,d)<0 && orient(c,d,a)*orient(c,d,b)<0
            if (crosses || onSegment(a,b,c) || onSegment(a,b,d) || onSegment(c,d,a) || onSegment(c,d,b)) return "Boundary crosses itself. Move or remove incorrect points."
        }
        if (area(points) < 0.1) return "Boundary has no measurable area. Select non-collinear points."
        return null
    }
}

data class GpsSample(val point: GeoPoint, val accuracyMeters: Double, val elapsedMillis: Long)
data class GpsFilterConfig(val maximumAccuracyMeters: Double = 15.0, val minimumDistanceMeters: Double = 2.0, val maximumSpeedMetersPerSecond: Double = 8.0, val maximumGapMillis: Long = 30_000, val maximumResumeDistanceMeters: Double = 30.0)
class GpsPointFilter(private val config: GpsFilterConfig = GpsFilterConfig(), private val resumePoint: GeoPoint? = null) {
    var previous: GpsSample? = null
        private set
    private var reliable: GpsSample? = null
    val lastReliableMillis: Long? get() = reliable?.elapsedMillis
    fun accept(sample: GpsSample): Boolean {
        if (!sample.point.valid || !sample.accuracyMeters.isFinite() || sample.accuracyMeters <= 0 || sample.accuracyMeters > config.maximumAccuracyMeters) return false
        if(previous == null && resumePoint != null && FieldGeometry.distance(resumePoint,sample.point) > config.maximumResumeDistanceMeters) return false
        reliable?.let {
            val dt = sample.elapsedMillis-it.elapsedMillis
            val distance = FieldGeometry.distance(it.point, sample.point)
            if (dt <= 0 || dt > config.maximumGapMillis || distance / (dt / 1000.0) > config.maximumSpeedMetersPerSecond) return false
        }
        // A reliable stationary fix refreshes signal health but adds no point.
        // Compare speed with the last reliable fix, not a potentially old
        // recorded point, so waiting cannot make a sudden jump seem plausible.
        reliable = sample
        previous?.let { if(FieldGeometry.distance(it.point,sample.point) < config.minimumDistanceMeters) return false }
        previous = sample
        return true
    }
}
