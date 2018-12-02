package com.pavlus.raytrace.model.texture

import com.pavlus.raytrace.Color
import com.pavlus.raytrace.model.FV3
import com.pavlus.raytrace.model.math.Perlin
import com.pavlus.raytrace.model.math.times
import kotlin.math.sin
import kotlin.math.sqrt

class NoiseTexture(private val noise: Perlin, scale: Number = 1.0) : Texture {
    val scale = scale.toDouble()
    private val offset = -sqrt(2.0) / 2.0
    private val invRange = 1.0 / sqrt(2.0)
    override fun color(u: Number, v: Number, position: FV3): Color {
        val noise = noise.noise(position * scale)
        val scaled = (noise - offset) * invRange
        val result = Color(scaled, scaled, scaled)
        return result
    }
}


class MarbleTexture(private val noise: Perlin, scale: Number = 1.0) : Texture {
    val scale = scale.toDouble()
    override fun color(u: Number, v: Number, position: FV3): Color {
        val offset = scale * position.z
        val scaledNoise = 10 * noise.turb(position)
        val component = 0.5 * (1 + sin(scaledNoise + offset))
        val result = Color(component, component, component)
        return result
    }
}