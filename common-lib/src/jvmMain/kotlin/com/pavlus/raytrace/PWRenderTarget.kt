package com.pavlus.raytrace;


import javafx.scene.canvas.Canvas
import javafx.scene.image.PixelWriter
import kotlin.math.roundToInt


class PWRenderTarget(override val width: Int, override val height: Int, private val writer: PixelWriter)
    : RenderTarget() {
    override fun put(x: Int, y: Int, color: Color) {
        writer.setArgb(x, y, color.toArgb())
    }

    companion object {
        fun from(c: Canvas) = PWRenderTarget(c.width.toInt(), c.height.toInt(), c.graphicsContext2D.pixelWriter)
    }
}

private const val opaque: Int = 0xFF000000.toInt()

private fun Color.toArgb(): Int {
    val red = (r * 255.0).roundToInt().shl(16)
    val green = (g * 255.0).roundToInt().shl(8)
    val blue = (b * 255.0).roundToInt()
    return opaque.or(red).or(green).or(blue)
}
