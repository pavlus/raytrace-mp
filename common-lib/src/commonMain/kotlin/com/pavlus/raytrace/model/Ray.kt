package com.pavlus.raytrace.model

import com.pavlus.raytrace.Color
import com.pavlus.raytrace.model.math.plus
import com.pavlus.raytrace.model.math.times

data class Ray(
    val origin: FV3,
    val direction: FV3,
    val atttenuation: Color = Color(1, 1, 1),
    val time:Double = 0.0,
    val ttl: Int = 50
) {
    fun produce(origin: FV3, direction: FV3, atttenuation: Color) = Ray(origin, direction, atttenuation, time = time, ttl = ttl - 1)
    fun produce(origin: FV3, direction: FV3) = Ray(origin, direction, time = time, ttl = ttl - 1)


    fun pointTo(directionScale: Number) = origin + direction * directionScale
}