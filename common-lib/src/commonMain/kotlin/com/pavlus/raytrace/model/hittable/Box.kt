package com.pavlus.raytrace.model.hittable

import com.pavlus.raytrace.Hittable
import com.pavlus.raytrace.model.Ray
import com.pavlus.raytrace.model.material.Material
import com.pavlus.raytrace.model.math.Point

open class Box(corner: Point, size: Point, val material: Material) : Hittable {
    protected val bb = Aabb(corner, size)
    protected val sides = Stage(
        listOf(
            XYRect(corner.x, size.x, corner.y, size.y, size.z, material),
            XYRect(corner.x, size.x, corner.y, size.y, corner.z, material).invertNormal(),
            XZRect(corner.x, size.x, corner.z, size.z, size.y, material),
            XZRect(corner.x, size.x, corner.z, size.z, corner.y, material).invertNormal(),
            YZRect(corner.y, size.y, corner.z, size.z, size.x, material),
            YZRect(corner.y, size.y, corner.z, size.z, corner.x, material).invertNormal()
        )
    )

    override fun boundingBox(time0: Number, time1: Number): Aabb = bb

    override fun getHit(ray: Ray, minDistance: Number, maxDistance: Number) =
        sides.getHit(ray, minDistance, maxDistance)


}

