package com.pavlus.raytrace.model.material

import com.pavlus.raytrace.Color
import com.pavlus.raytrace.model.FV3
import com.pavlus.raytrace.model.Hit
import com.pavlus.raytrace.model.Ray

abstract class Material {
    abstract fun scatter(ray: Ray, hit: Hit): Ray?
    open fun emmit(u: Double, v: Double, position: FV3): Color? = null
    fun emmit(direction: Pair<Double, Double>, position: FV3): Color? =
        emmit(direction.first, direction.second, position)

}