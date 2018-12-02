package com.pavlus.raytrace.model.texture

import com.pavlus.raytrace.Color
import com.pavlus.raytrace.model.FV3

class ColorTexture(val color: Color) : Texture {
    constructor(r: Number, g: Number, b: Number) : this(Color(r, g, b))

    override fun color(u: Number, v: Number, position: FV3) = color
}