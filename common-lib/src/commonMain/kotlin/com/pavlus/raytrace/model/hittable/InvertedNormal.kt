package com.pavlus.raytrace.model.hittable

import com.pavlus.raytrace.Hittable
import com.pavlus.raytrace.model.Hit
import com.pavlus.raytrace.model.Ray
import com.pavlus.raytrace.model.math.times

class InvertedNormal(val delegate: Hittable) : Hittable by delegate {
    override fun getHit(ray: Ray, minDistance: Number, maxDistance: Number): Hit? {
        val result = delegate.getHit(ray, minDistance, maxDistance) ?: return null
        return result.copy(normal = result.normal * -1)
    }
}

fun Hittable.invertNormal() = InvertedNormal(this)