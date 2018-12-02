package com.pavlus.raytrace

abstract class RenderTarget {
    abstract val width: Int
    abstract val height: Int
    abstract fun put(x: Int, y: Int, color: Color)
}