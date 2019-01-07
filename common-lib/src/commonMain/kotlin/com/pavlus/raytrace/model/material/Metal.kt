package com.pavlus.raytrace.model.material

import com.pavlus.raytrace.Color
import com.pavlus.raytrace.identity
import com.pavlus.raytrace.model.FV3
import com.pavlus.raytrace.model.Hit
import com.pavlus.raytrace.model.Ray
import com.pavlus.raytrace.model.math.*
import com.pavlus.raytrace.randomUnitVector

class Metal(val albedo: Color, fuzzFactor: Number) : Material() {
    val fuzz: (FV3) -> FV3 = if (fuzzFactor.toDouble() < 1.toDouble()) { vector->
        fuzz(
            fuzzFactor,
            vector
        )
    } else ::identity


    constructor(r: Number, g: Number, b: Number, fuzz: Number) : this(Color(r, g, b), fuzz)

    override fun scatter(ray: Ray, hit: Hit): Ray? {
        val reflected = ray.direction.unit().reflect(hit.normal)
        if (reflected.dot(hit.normal) <= 0) return null
        return ray.produce(hit.point, fuzz(reflected), atttenuation = albedo)
    }


    private fun fuzz(number: Number, vector: FV3) = randomUnitVector() * number + vector

}