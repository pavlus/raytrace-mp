package com.pavlus.raytrace.model.material

import com.pavlus.raytrace.Color
import com.pavlus.raytrace.model.FV3
import com.pavlus.raytrace.model.Hit
import com.pavlus.raytrace.model.Ray
import com.pavlus.raytrace.model.texture.ColorTexture
import com.pavlus.raytrace.model.texture.Texture

class DiffuseLight(val texture: Texture) : Material() {
    constructor(r: Number, g: Number, b: Number) : this(ColorTexture(r, g, b))

    override fun scatter(ray: Ray, hit: Hit): Ray? {
        return null
    }

    override fun emmit(u: Double, v: Double, position: FV3): Color? {
        return texture.color(u, v, position)
    }
}