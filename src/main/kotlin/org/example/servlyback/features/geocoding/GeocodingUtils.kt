package org.example.servlyback.features.geocoding

import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel

class GeocodingUtils {
    companion object {
        private val geometryFactory = GeometryFactory(PrecisionModel(), 4326)

        fun createPoint(lat: Double, lng: Double): Point {
            return geometryFactory.createPoint(Coordinate(lng, lat))
        }
    }
}