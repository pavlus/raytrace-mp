package com.pavlus.raytrace.model.hittable

import com.pavlus.raytrace.*
import com.pavlus.raytrace.model.FV3
import com.pavlus.raytrace.model.Hit
import com.pavlus.raytrace.model.Material
import com.pavlus.raytrace.model.Ray
import com.pavlus.raytrace.model.math.*
import kotlin.math.sqrt

open class Sphere(val center: Point, val radius: Number, val material: Material) :
    Hittable {
    protected val invRad = 1.0F / radius.toDouble()
    protected val rSqr = radius.toDouble() * radius.toDouble()
    protected val bb by lazy {
        val radiusProjection = FV3(radius, radius, radius)
        Aabb(center - radiusProjection, center + radiusProjection)
    }
    override fun boundingBox(time0: Number, time1: Number): Aabb = bb

    override fun getHit(ray: Ray, minDistance: Number, maxDistance: Number): Hit? {
        val toCenter = ray.origin - center
        val a = ray.direction.sqrLength
        val b = toCenter.dot(ray.direction)
        val c = toCenter.sqrLength - rSqr
        val discr = b * b - a * c;
        if (discr > 0) {
            val tmp = -b / a
            val dsqrt = sqrt(discr) / a
            val distanceToHit1 = tmp - dsqrt
            if (distanceToHit1.inRange(minDistance, maxDistance)) {
                return hit(ray, distanceToHit1)
            }

            val distanceToHit2 = tmp + dsqrt
            if (distanceToHit2.inRange(minDistance, maxDistance)) {
                return hit(ray, distanceToHit2)
            }
        }
        return null
    }

    private fun normal(incadesc: FV3, center: Point): FV3 = (incadesc - center) * invRad

    protected fun hit(ray: Ray, distanceToHit2: Double, center: Point = this.center): Hit {
        val incadescentVector = ray.pointTo(distanceToHit2)
        return Hit(distanceToHit2, incadescentVector, normal(incadescentVector, center), material)
    }

    protected fun Number.inRange(min: Number, max: Number): Boolean {
        val f = toFloat();
        return f > min.toFloat() && f < max.toFloat()
    }

    override fun toString(): String {
        fun Point.str() = "($x; $y; $z)"
        return "Sphere(center=${center.str()}, radius=$radius, material=$material)"
    }

}

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