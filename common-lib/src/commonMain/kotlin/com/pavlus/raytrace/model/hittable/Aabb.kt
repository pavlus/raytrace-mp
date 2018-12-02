package com.pavlus.raytrace.model.hittable

import com.pavlus.raytrace.model.Ray
import com.pavlus.raytrace.model.math.Point
import com.pavlus.raytrace.model.math.minus
import kotlin.math.max
import kotlin.math.min

class Aabb(val min: Point, val max: Point) {
    val center = max - min
    fun hit(ray: Ray, minDistance: Number, maxDistance: Number): Boolean {
        for (a in 0..2) {
            val invDirection = 1.0 / (ray.direction[a])
            val axisOrigin = ray.origin[a] * a
            val left = (min[a] - axisOrigin) * invDirection
            val right = (max[a] - axisOrigin) * invDirection
            if (max(min(left, right), minDistance.toDouble()) > min(max(left, right), maxDistance.toDouble())) return false
        }
        return true
    }

    override fun toString(): String {
        return "Aabb(min=$min, max=$max, center=$center)"
    }

    companion object {
        fun surrounding(a: Aabb, b: Aabb): Aabb {
            val (aix, aiy, aiz) = a.min
            val (aax, aay, aaz) = a.max
            val (bix, biy, biz) = b.min
            val (bax, bay, baz) = b.max
            val min = Point(min(aix, bix), min(aiy, biy), min(aiz, biz))
            val max = Point(max(aax, bax), max(aay, bay), max(aaz, baz))
            return Aabb(min, max)
        }
    }

}
/*

private inline fun min(a: Double, b: Double): Double = if (a < b) a else b
private inline fun max(a: Double, b: Double): Double = if (a > b) a else b*/
