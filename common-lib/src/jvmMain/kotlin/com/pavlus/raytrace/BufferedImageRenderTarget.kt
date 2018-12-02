package com.pavlus.raytrace;

import java.awt.image.BufferedImage
import kotlin.math.roundToInt

class BufferedImageRenderTarget(override val width: Int, override val height: Int)
    : RenderTarget() {

    val buffer = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

    init {
        println(buffer.propertyNames)
    }

    override fun put(x: Int, y: Int, color: Color) {
        buffer.setRGB(x, y, color.toRgb())
    }

    private fun Color.toRgb(): Int {
        val red = (r * 255.0).roundToInt().shl(16)
        val green = (g * 255.0).roundToInt().shl(8)
        val blue = (b * 255.0).roundToInt()

        return red or green or blue
    }

}
