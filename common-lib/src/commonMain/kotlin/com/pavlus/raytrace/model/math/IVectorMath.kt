package com.pavlus.raytrace.model.math

import com.pavlus.raytrace.Color
import com.pavlus.raytrace.model.FV3

interface IVectorMath {
    fun unaryMinus(self: FV3): FV3
    fun plus(self: FV3, other: FV3): FV3
    fun plus(self: FV3, offset: Number): FV3
    fun minus(self: FV3, offset: Number): FV3
    fun minus(self: FV3, other: FV3): FV3
    fun times(self: FV3, other: FV3): FV3
    fun times(self: FV3, scale: Number): FV3
    fun dot(self: FV3, other: FV3): Double

    fun cross(self: FV3, other: FV3): FV3
    fun unit(self: FV3): FV3
    fun sqrLength(self: FV3): Double
    fun length(self: FV3): Double
    fun toColor(self: FV3): Color
    fun unitToColor(self: FV3): Color
    fun reflect(self: FV3, normal: FV3): FV3
    fun pow(fl: Double, n: Number): Double
    fun div(number: Number, other: Double): Double
}