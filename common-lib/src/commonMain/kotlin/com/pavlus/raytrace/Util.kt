package com.pavlus.raytrace

import com.pavlus.raytrace.model.FV3
import com.pavlus.raytrace.model.math.nextRandom
import com.pavlus.raytrace.model.texture.RasterTexture
import kotlin.math.PI
import kotlin.random.Random

const val PI2 = 2 * PI
const val PI_HALF = PI / 2

val Pair<Double, Double>.u get() = first
val Pair<Double, Double>.v get() = second

expect fun loadTexture(name: String): RasterTexture

val randomizer = Random(0)

fun randomUnitVector(): FV3 {
    var x: Double
    var y: Double
    var z: Double
    do {
        x = nextRandom() * 2 - 1
        y = nextRandom() * 2 - 1
        z = nextRandom() * 2 - 1
    } while (x * x + y * y + z * z >= 1)
    return FV3(x, y, z)
}

fun <T> identity(value: T): T = value
