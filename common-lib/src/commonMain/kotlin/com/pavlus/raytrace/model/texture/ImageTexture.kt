package com.pavlus.raytrace.model.texture

import com.pavlus.raytrace.Color
import com.pavlus.raytrace.model.FV3
import kotlin.math.roundToInt

class ImageTexture(private val raster: RasterTexture) : Texture {
    private val nx: Int = raster.width
    private val ny: Int = raster.height

    override fun color(u: Number, v: Number, position: FV3): Color {
        val i = (nx * u.toDouble()).roundToInt().coerceIn(0, nx - 1)
        val j = (ny * v.toDouble()).roundToInt().coerceIn(0, ny - 1)
        return raster.colorAt(i, j)
    }
}

interface RasterTexture {
    val width: Int
    val height: Int
    fun colorAt(x: Int, y: Int): Color
}