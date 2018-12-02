package com.pavlus.raytrace.model

import com.pavlus.raytrace.model.math.*
import kotlin.math.PI
import kotlin.math.tan

data class Camera(
    val bottomLeftCorner: FV3 = FV3(-2, -1, -1),
    val uScale: FV3 = FV3(4, 0, 0),
    val vScale: FV3 = FV3(0, 2, 0),
    val origin: FV3 = FV3(0, 0, 0),
    val lensRadius: Double,
    val rayTTL: Int,
    val t0: Double = 0.0,
    val t1: Double = 0.0
) {
    private val timespan = t1 - t0

    fun getRay(u: Number, v: Number): Ray {
        val s = u.toDouble()
        val t = v.toDouble()
        val rd = randomUnitDisk() * lensRadius
        val offset = s * rd.x + t * rd.y
        val time = t0 + nextRandom() * timespan
        val direction = bottomLeftCorner + uScale * s + vScale * t - origin

        return Ray(origin + offset, direction - offset, time = time, ttl = rayTTL)
    }

    companion object {
        fun camera(
            from: FV3, at: FV3, vup: FV3,
            vFov: Double, aspect: Double,
            aperture: Double, focusDistance: Double,
            rayTTL: Int,
            t0: Double,
            t1: Double
        ): Camera {
            val theta = vFov * PI / 180.0
            val halfHeight = tan(theta / 2)
            val halfWidth = aspect * halfHeight

            val w = (from - at).unit()
            val u = vup.cross(w)
            val v = w.cross(u)

            val lensRadius = aperture / 2
            return Camera(
                bottomLeftCorner = from
                        - (u * focusDistance * halfWidth)
                        - (v * focusDistance * halfHeight)
                        - w * focusDistance,
                uScale = u * halfWidth * focusDistance * 2,
                vScale = v * halfHeight * focusDistance * 2,
                origin = from,
                lensRadius = lensRadius,
                rayTTL = rayTTL,
                t0 = t0,
                t1 = t1
            )
        }
    }
}


fun randomUnitDisk(): FV3 {
    var x: Double
    var y: Double
    var z: Double
    var cntr = 0
    do {
        x = randomizer.nextDouble() * 2 - 1
        y = randomizer.nextDouble() * 2 - 1
        z = 0.0
//        if (cntr++ > 15) println("$cntr")
    } while (x * x + y * y + z * z >= 1)
    return FV3(x, y, z)
}