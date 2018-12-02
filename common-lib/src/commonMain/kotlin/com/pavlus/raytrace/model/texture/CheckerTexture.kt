package com.pavlus.raytrace.model.texture

import com.pavlus.raytrace.Color
import com.pavlus.raytrace.model.FV3
import kotlin.math.sin

class CheckerTexture(val even: Texture, val odd: Texture, val scale: Number = 10) : Texture {
    override fun color(u: Number, v: Number, position: FV3): Color {
        val (x, y, z) = position
        val sines = sin(scale.toDouble() * x) * sin(scale.toDouble() * y) * sin(scale.toDouble() * z)
        return if (sines < 0) odd.color(u, v, position)
        else even.color(u, v, position)
    }

}