package com.pavlus.raytrace.model.math

import com.pavlus.raytrace.model.FV3
import com.pavlus.raytrace.randomizer
import kotlin.math.abs
import kotlin.math.floor

class Perlin() {
    private val lookup = (0..255).map { randomUnitVector() }
    private val permX = (0..255).shuffled(randomizer)
    private val permY = (0..255).shuffled(randomizer)
    private val permZ = (0..255).shuffled(randomizer)

    fun noise(position: FV3): Double {
        val (x, y, z) = position

        val i = floor(x).toInt()
        val j = floor(y).toInt()
        val k = floor(z).toInt()

        val u = x - floor(x)
        val v = y - floor(y)
        val w = z - floor(z)

        // @formatter:off
        val c = Array(2) { di -> Array(2) { dj -> Array(2) { dk ->
            lookup[ permX[(i + di) and 0xff]
                xor permY[(j + dj) and 0xff]
                xor permZ[(k + dk) and 0xff]
            ] } } }
        // @formatter:on
        return trilinp(c, u, v, w)
    }

    fun turb(vector: FV3, depth: Int = 7): Double {
        var result = 0.0
        var weight = 1.0
        var tmpVec = vector
        for (int in 1..depth) {
            result += weight * noise(tmpVec)
            weight *= 0.5
            tmpVec *= 2
        }
        return abs(result)
    }


    private fun trilinp(c: Array<Array<Array<FV3>>>, u: Double, v: Double, w: Double): Double {
        var accum = 0.0

        val uu = u * u * (3.0 - 2.0 * u)
        val vv = v * v * (3.0 - 2.0 * v)
        val ww = w * w * (3.0 - 2.0 * w)
        // @formatter:off
        for (i in 0..1) for (j in 0..1) for (k in 0..1) {
            val weight = FV3(u - i, v - j, w - k)
            accum += (i * uu + (1 - i) * (1 - uu)) *
                     (j * vv + (1 - j) * (1 - vv)) *
                     (k * ww + (1 - k) * (1 - ww)) * c[i][j][k].dot(weight)
        }
        // @formatter:on
        return accum
    }

    private fun randomUnitVector(): FV3 = FV3(2 * nextRandom() - 1, 2 * nextRandom() - 1, 2 * nextRandom() - 1).unit()

}