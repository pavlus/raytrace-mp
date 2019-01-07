package com.pavlus.raytrace.model.material

import com.pavlus.raytrace.model.FV3
import com.pavlus.raytrace.model.Hit
import com.pavlus.raytrace.model.Ray
import com.pavlus.raytrace.model.math.*
import kotlin.math.sqrt

class Dielectric(val refraction: Double) : Material() {
    constructor(refraction: Number) : this(refraction.toDouble())

    private fun schlick(cosine: Double, refraction: Double): Double {
        var r0 = (1 - refraction) / (1 + refraction)
        r0 *= r0
        return r0 + (1 - r0) * (1 - cosine).pow(5.0)
    }

    private fun refract(v: FV3, normal: FV3, niOverNt: Double): FV3? {
        val unit = v.unit()
        val dt = unit.dot(normal)
        // TODO: optimize calculations
        val discriminant = 1.0 - niOverNt * niOverNt * (1 - dt * dt)
        return if (discriminant > 0) {
            (v - normal * dt) * niOverNt - normal * sqrt(discriminant)
        } else null
    }

    override fun scatter(ray: Ray, hit: Hit): Ray? {
        try {
            val outwardNormal: FV3
            val refracted: FV3?
            val niOverNt: Double
            val cosine: Double
            val reflectionProb: Double
            val unitDirection = ray.direction.unit()
            val vectorCosine = unitDirection.dot(hit.normal)

            if (vectorCosine > 0) {
                outwardNormal = -hit.normal
                niOverNt = refraction
                cosine = sqrt(1 - refraction * refraction * (1 - vectorCosine * vectorCosine))
            } else {
                outwardNormal = hit.normal
                niOverNt = 1.0 / refraction
                cosine = -vectorCosine
            }

            refracted = refract(unitDirection, outwardNormal, niOverNt)
            val reflected = ray.direction.reflect(hit.normal)
            if (refracted != null) {
                reflectionProb = schlick(cosine, refraction)
            } else {
                return ray.produce(hit.point, reflected)
            }
            return if (nextRandom() < reflectionProb) {
                ray.produce(hit.point, reflected)
            } else {
                ray.produce(hit.point, refracted)
            }
        } catch (e: Exception) {
            println(e)
            throw e
        }

    }

}