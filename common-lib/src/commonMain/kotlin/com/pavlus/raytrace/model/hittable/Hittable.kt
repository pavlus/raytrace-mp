package com.pavlus.raytrace

import com.pavlus.raytrace.model.Hit
import com.pavlus.raytrace.model.Ray
import com.pavlus.raytrace.model.hittable.Aabb


interface Hittable {
    fun getHit(ray: Ray, minDistance: Number, maxDistance: Number): Hit?
    fun boundingBox(time0: Number, time1: Number): Aabb?
}

