package com.pavlus.raytrace.model.hittable

import com.pavlus.raytrace.Hittable
import com.pavlus.raytrace.model.FV3
import com.pavlus.raytrace.model.Hit
import com.pavlus.raytrace.model.Ray
import com.pavlus.raytrace.model.material.Material
import com.pavlus.raytrace.model.math.Point

class XYRect(x0: Number, x1: Number, y0: Number, y1: Number, k: Number, material: Material) :
    InPlaneRect(0, x0, x1, 1, y0, y1, k, material) {
}


class XZRect(x0: Number, x1: Number, z0: Number, z1: Number, k: Number, material: Material) :
    InPlaneRect(0, x0, x1, 2, z0, z1, k, material) {
}


class YZRect(y0: Number, y1: Number, z0: Number, z1: Number, k: Number, material: Material) :
    InPlaneRect(1, y0, y1, 2, z0, z1, k, material) {
}

sealed class InPlaneRect(
    val a: Int,
    val a0: Double,
    val a1: Double,
    val b: Int,
    val b0: Double,
    val b1: Double,
    val k: Double,
    val material: Material
) : Hittable {
    val c = 3 - a - b
    val normal = NORMALS[c]

    constructor(a: Int, a0: Number, a1: Number, b: Int, b0: Number, b1: Number, k: Number, material: Material)
            : this(a, a0.toDouble(), a1.toDouble(), b, b0.toDouble(), b1.toDouble(), k.toDouble(), material)

    val bbox = Aabb(dispatch(a0, b0, k - 0.1, a, b), dispatch(a1, b1, k + 0.1, a, b))
    override fun boundingBox(time0: Number, time1: Number) = bbox

    val aSpan = a1 - a0
    val bSpan = b1 - b0
    override fun getHit(ray: Ray, minDistance: Number, maxDistance: Number): Hit? {
        val distance = (k - ray.origin[c]) / ray.direction[c]
        if (minDistance.toDouble() > distance && distance > maxDistance.toDouble())
            return null

        val aHit = ray.origin[a] + ray.direction[a] * distance
        val bHit = ray.origin[b] + ray.direction[b] * distance

        if (aHit < a0 || aHit > a1 || bHit < b0 || bHit > b1) {
            return null
        }

        val u = (a - a0) / aSpan
        val v = (b - b0) / bSpan
        return Hit(distance, ray.scale(distance), normal, material, Pair(u, v))

    }

    companion object {
        private val NORMALS = arrayOf(
            FV3(1, 0, 0),
            FV3(0, 1, 0),
            FV3(0, 0, 1)
        )

        fun dispatch(a: Double, b: Double, c: Double, ai: Int, bi: Int): Point {
            check(ai in 0..2)
            check(bi in 0..2)
            check(ai != bi)
            val ci = 3 - ai - bi
            val vals = DoubleArray(3) {
                when (it) {
                    ai -> a
                    bi -> b
                    ci -> c
                    else -> throw IllegalStateException("check() failed to check parameters, shouldn't happen")
                }
            }
            return Point(vals[0], vals[1], vals[2])
        }
    }
}
