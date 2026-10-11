package com.harvestpay.app.measurement

import org.junit.Assert.*
import org.junit.Test
import kotlin.math.*

class FieldGeometryTest {
    private val square=listOf(GeoPoint(0.0,0.0),GeoPoint(0.0,.001),GeoPoint(.001,.001),GeoPoint(.001,0.0))
    @Test fun referenceSquareMatchesKnownGeodesicArea() {
        // 0.001 degrees at the equator is about 111.195m per edge.
        assertEquals(12364.3466,FieldGeometry.area(square),.01)
        assertNull(FieldGeometry.validationError(square))
    }
    @Test fun areaIsIndependentOfWindingAndStartingCorner() {
        assertEquals(FieldGeometry.area(square),FieldGeometry.area(square.reversed()),.00001)
        assertEquals(FieldGeometry.area(square),FieldGeometry.area(square.drop(1)+square.first()),.00001)
    }
    @Test fun sphericalOctantHasKnownExactArea() {
        val octant=listOf(GeoPoint(0.0,0.0),GeoPoint(0.0,90.0),GeoPoint(90.0,0.0))
        assertEquals(PI/2*FieldGeometry.EARTH_RADIUS.pow(2),FieldGeometry.area(octant),1.0)
    }
    @Test fun conversionsRetainPrecisionAndRequireExplicitPositiveFactor() {
        val area=FieldGeometry.SQUARE_METERS_PER_ACRE*2.35
        assertEquals(2.35,FieldGeometry.acres(area),1e-12)
        assertEquals(3.76,FieldGeometry.bigha(area,1.6),1e-12)
        assertEquals(area/10000,FieldGeometry.hectares(area),1e-12)
        listOf(0.0,-1.0,Double.NaN,Double.POSITIVE_INFINITY).forEach { assertThrows(IllegalArgumentException::class.java) { FieldGeometry.bigha(area,it) } }
    }
    @Test fun rejectsCrossingDuplicateCollinearAndInvalidPolygons() {
        assertNotNull(FieldGeometry.validationError(listOf(square[0],square[2],square[1],square[3])))
        assertNotNull(FieldGeometry.validationError(square+square.last()))
        assertNotNull(FieldGeometry.validationError(listOf(GeoPoint(0.0,0.0),GeoPoint(0.0,.001),GeoPoint(0.0,.002))))
        assertNotNull(FieldGeometry.validationError(square+GeoPoint(Double.NaN,0.0)))
        assertNotNull(FieldGeometry.validationError(square.take(2)))
    }
    @Test fun handlesDateLineWithoutHugeBoundingBoxArea() {
        val dateLine=listOf(GeoPoint(0.0,179.9995),GeoPoint(0.0,-179.9995),GeoPoint(.001,-179.9995),GeoPoint(.001,179.9995))
        assertNull(FieldGeometry.validationError(dateLine))
        assertEquals(FieldGeometry.area(square),FieldGeometry.area(dateLine),.01)
    }
    @Test fun measurementDoesNotChangeExistingRoundBillingFormula() {
        assertEquals(2.0,com.harvestpay.app.domain.BusinessCalculator.workDoneBigha(4.0,.5),0.0)
        assertEquals(400.0,com.harvestpay.app.domain.BusinessCalculator.subtotal(4.0,200.0,.5),0.0)
    }
    @Test fun gpsFilterRejectsNoiseAndSuddenJumps() {
        val f=GpsPointFilter()
        assertFalse(f.accept(GpsSample(square[0],60.0,1000)))
        assertTrue(f.accept(GpsSample(square[0],3.0,1000)))
        assertFalse(f.accept(GpsSample(square[0],3.0,2000)))
        assertFalse(f.accept(GpsSample(GeoPoint(0.0,.01),3.0,3000)))
        assertTrue(f.accept(GpsSample(GeoPoint(0.0,.00003),3.0,4000)))
        assertFalse(f.accept(GpsSample(GeoPoint(0.0,.00006),3.0,35000)))
    }
    @Test fun stationaryReliableFixesDoNotBecomeSignalLossOrPermitASuddenJump() {
        val f=GpsPointFilter()
        val point=GeoPoint(25.0,80.0)
        assertTrue(f.accept(GpsSample(point,3.0,1000)))
        (2..40).forEach { assertFalse(f.accept(GpsSample(point,3.0,it*1000L))) }
        assertEquals(40000L,f.lastReliableMillis)
        assertFalse(f.accept(GpsSample(GeoPoint(25.001,80.0),3.0,41000)))
        assertTrue(f.accept(GpsSample(GeoPoint(25.00003,80.0),3.0,42000)))
    }
    @Test fun resumedTrackCannotBridgeAJumpFromTheLastBoundaryPoint() {
        val f=GpsPointFilter(resumePoint=GeoPoint(25.0,80.0))
        assertFalse(f.accept(GpsSample(GeoPoint(25.01,80.0),3.0,1000)))
        assertTrue(f.accept(GpsSample(GeoPoint(25.00003,80.0),3.0,2000)))
    }
    @Test fun gpsFilterSupportsConfigurableAccuracyAndRejectsStaleTime() {
        val f=GpsPointFilter(GpsFilterConfig(maximumAccuracyMeters=5.0))
        assertFalse(f.accept(GpsSample(square[0],6.0,1000)))
        assertTrue(f.accept(GpsSample(square[0],4.0,1000)))
        assertFalse(f.accept(GpsSample(GeoPoint(0.0,.00003),4.0,1000)))
    }
}
