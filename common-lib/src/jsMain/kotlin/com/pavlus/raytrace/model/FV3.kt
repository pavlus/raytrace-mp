package com.pavlus.raytrace.model

actual data class FV3 actual constructor(actual val x: Float, actual val y: Float, actual val z: Float) {
    actual constructor(x: Number, y: Number, z: Number) : this(x.toFloat(), y.toFloat(), z.toFloat())
}