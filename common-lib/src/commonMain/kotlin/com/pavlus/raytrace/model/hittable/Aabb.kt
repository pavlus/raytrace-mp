package com.pavlus.raytrace.model.hittable

import com.pavlus.raytrace.model.Ray
import com.pavlus.raytrace.model.math.Point
import kotlin.math.max
import kotlin.math.min

/**
 * Axis-Aligned Bounding Box implementation
 */
class Aabb(val min: Point, val max: Point) {
    fun hit(ray: Ray, minDistance: Number, maxDistance: Number): Boolean {
        for (a in 0..2) {
            val invDirection = 1.0 / ray.direction[a]
            val axisOrigin = ray.origin[a]
            val left = (min[a] - axisOrigin) * invDirection
            val right = (max[a] - axisOrigin) * invDirection
            if (max(min(left, right), minDistance.toDouble()) > min(max(left, right), maxDistance.toDouble())) return false
        }
        return true
    }

    override fun toString(): String {
        return "Aabb(min=$min, max=$max)"
    }

    /**
     * Join boxes into single box
     */
    operator fun plus(other:Aabb):Aabb{
        val (aix, aiy, aiz) = this.min
        val (aax, aay, aaz) = this.max
        val (bix, biy, biz) = other.min
        val (bax, bay, baz) = other.max
        val min = Point(min(aix, bix), min(aiy, biy), min(aiz, biz))
        val max = Point(max(aax, bax), max(aay, bay), max(aaz, baz))
        return Aabb(min, max)
    }

}
/*

private inline fun min(a: Double, b: Double): Double = if (a < b) a else b
private inline fun max(a: Double, b: Double): Double = if (a > b) a else b*/
