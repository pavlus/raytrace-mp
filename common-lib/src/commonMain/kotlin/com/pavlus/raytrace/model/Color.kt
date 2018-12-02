package com.pavlus.raytrace

import com.pavlus.raytrace.model.FV3

data class Color(val r: Double, val g: Double, val b: Double) {
    constructor(r: Number, g: Number, b: Number) : this(r.toDouble(), g.toDouble(), b.toDouble())
}
inline operator fun Color.times(other: Color) = Color(r * other.r, g * other.g, b * other.b)
inline operator fun Color.times(other: Number): Color {
    val f = other.toFloat()
    return Color(r * f, g * f, b * f)
}

inline operator fun Color.div(other: Color) = FV3(r / other.r, g / other.g, b / other.b)
inline operator fun Color.plus(other: Color) = Color(r + other.r, g + other.g, b + other.b)