package com.pavlus.raytrace.model.hittable

import com.pavlus.raytrace.Hittable
import com.pavlus.raytrace.model.Hit
import com.pavlus.raytrace.model.Ray

class Stage(private val children: Collection<Hittable>) : Hittable {

    override fun getHit(ray: Ray, minDist: Number, maxDist: Number): Hit? {
        var hit: Hit? = null
        var closest: Double = maxDist.toDouble()
        val min = minDist.toDouble()
        children.forEach {
            it.getHit(ray, min, closest)?.let {
                hit = it
                closest = it.distance
            }
        }
        return hit
    }


    override fun boundingBox(time0: Number, time1: Number): Aabb? {
        return aabb(children, time0, time1)
    }

}


fun aabb(children: Collection<Hittable>, time0: Number, time1: Number): Aabb? {
    return children.mapNotNull {
        it.boundingBox(time0, time1)
    }.ifEmpty {
        null
    }?.reduce(Aabb::plus)
}


