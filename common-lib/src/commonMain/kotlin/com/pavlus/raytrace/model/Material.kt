package com.pavlus.raytrace.model

import com.pavlus.raytrace.Color
import com.pavlus.raytrace.model.math.*
import com.pavlus.raytrace.model.texture.ColorTexture
import com.pavlus.raytrace.model.texture.Texture
import kotlin.math.sqrt
import kotlin.random.Random

interface Material {
    fun scatter(ray: Ray, hit: Hit): Ray?
}

class Lambertian(val albedo: Texture) : Material {

    constructor(r: Number, g: Number, b: Number) : this(ColorTexture(r, g, b))
    constructor(color: Color) : this(ColorTexture(color))

    override fun scatter(ray: Ray, hit: Hit): Ray? {
        val target = hit.normal + randomUnitVector()
        return ray.produce(hit.point, target, albedo.color(0, 0, hit.point))
    }

}


private fun Number.fuzz(vector: FV3) = randomUnitVector() * this + vector
fun <T> identity(value: T): T = value
class Metal(val albedo: Color, fuzzFactor: Number) : Material {
    val fuzz: (FV3) -> FV3 = if (fuzzFactor.toDouble() < 1.toDouble()) { vector: FV3 ->fuzzFactor.fuzz(vector)} else { vec->vec }


    constructor(r: Number, g: Number, b: Number, fuzz: Number) : this(Color(r, g, b), fuzz)

    override fun scatter(ray: Ray, hit: Hit): Ray? {
        val reflected = ray.direction.unit().reflect(hit.normal)
        if (reflected.dot(hit.normal) <= 0) return null
        return ray.produce(hit.point, fuzz(reflected), atttenuation = albedo)
    }

}

class Dielectric(val refraction: Double) : Material {
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
            val check = ray.direction.dot(hit.normal)
            if (check > 0) {
                outwardNormal = -hit.normal
                niOverNt = refraction
                cosine = refraction * check / ray.direction.unit().length
            } else {
                outwardNormal = hit.normal
                niOverNt = 1.0 / refraction
                cosine = -refraction * check / ray.direction.unit().length
            }

            refracted = refract(ray.direction, outwardNormal, niOverNt)
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

fun randomUnitVector(): FV3 {
    var cntr = 0;  // debug
    var x: Double
    var y: Double
    var z: Double
    do {
        x = nextRandom() * 2 - 1
        y = nextRandom() * 2 - 1
        z = nextRandom() * 2 - 1
//            if(cntr++>15) println("$cntr")
    } while (x * x + y * y + z * z >= 1)
    return FV3(x, y, z)
}


val randomizer = Random(0)
