package com.pavlus.raytrace.model.material

import com.pavlus.raytrace.Color
import com.pavlus.raytrace.model.Hit
import com.pavlus.raytrace.model.Ray
import com.pavlus.raytrace.model.math.plus
import com.pavlus.raytrace.model.math.times
import com.pavlus.raytrace.model.texture.ColorTexture
import com.pavlus.raytrace.model.texture.Texture
import com.pavlus.raytrace.randomUnitVector

open class Lambertian(val albedo: Texture) : Material() {

    constructor(r: Number, g: Number, b: Number) : this(ColorTexture(r, g, b))
    constructor(color: Color) : this(ColorTexture(color))

    override fun scatter(ray: Ray, hit: Hit): Ray? {
        val (u, v) = hit.uv
        val target = (hit.normal + randomUnitVector()) * 0.5
        return ray.produce(hit.point, target, albedo.color(u, v, hit.point))
    }

}