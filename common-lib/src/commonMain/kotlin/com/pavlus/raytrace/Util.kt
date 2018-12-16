package com.pavlus.raytrace

import com.pavlus.raytrace.model.texture.RasterTexture
import kotlin.math.PI

const val PI2 = 2 * PI
const val PI_HALF = PI / 2

val Pair<Double, Double>.u get() = first
val Pair<Double, Double>.v get() = second

expect fun loadTexture(name: String): RasterTexture