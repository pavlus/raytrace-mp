package com.pavlus.raytrace.model.hittable

import com.pavlus.raytrace.model.FV3
import com.pavlus.raytrace.model.Hit
import com.pavlus.raytrace.model.Material
import com.pavlus.raytrace.model.Ray
import com.pavlus.raytrace.model.math.*
import kotlin.math.sqrt

class MovingSphere(
    center0: Point, val center1: Point,
    t0: Number, t1: Number,
    radius: Number,
    material: Material
) : Sphere(center0, radius, material) {

    val time0 = t0.toDouble()
    val time1 = t1.toDouble()
    fun center(time: Double): FV3 {
        return center + (center1 - center) * ((time - time0) / (time1 - time0))
    }

    override fun getHit(ray: Ray, minDist: Number, maxDist: Number): Hit? {
        val center = center(ray.time)
        val toCenter = ray.origin - center
        val a = ray.direction.sqrLength
        val b = toCenter.dot(ray.direction)
        val c = toCenter.sqrLength - rSqr
        val discr = b * b - a * c;
        if (discr > 0) {
            val tmp = -b / a
            val dsqrt = sqrt(discr) / a
            val distanceToHit1 = tmp - dsqrt
            if (distanceToHit1.inRange(minDist, maxDist)) {
                return hit(ray, distanceToHit1, center)
            }

            val distanceToHit2 = tmp + dsqrt
            if (distanceToHit2.inRange(minDist, maxDist)) {
                return hit(ray, distanceToHit2, center)
            }
        }
        return null
    }

    override fun toString(): String {
        fun Point.str() = "($x; $y; $z)"
        return "MovingSphere(center=${center.str()}, center1=${center1.str()}, radius=$radius, material=$material, time0=$time0, time1=$time1)"
    }


}