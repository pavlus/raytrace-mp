package com.pavlus.raytrace

import com.pavlus.raytrace.model.texture.RasterTexture
import java.awt.image.BufferedImage

class BufferedImageRasterTexture(val image: BufferedImage) : RasterTexture {
    override val width: Int get() = image.width
    override val height: Int get() = image.height
    override fun colorAt(x: Int, y: Int) = image.getRGB(x, y).rgbToColor()
}

private fun Int.rgbToColor(): Color {
    val b = and(0xff)
    val g = shr(8).and(0xff)
    val r = shr(16).and(0xff)
    return Color(r / 255.0, g / 255.0, b / 255.0)
}