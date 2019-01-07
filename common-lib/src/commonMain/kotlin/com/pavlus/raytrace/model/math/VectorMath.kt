package com.pavlus.raytrace.model.math

import com.pavlus.raytrace.model.FV3
import com.pavlus.raytrace.randomizer

fun nextRandom(): Double = randomizer.nextDouble()

typealias Point = FV3

expect object VectorMath : IVectorMath


operator fun FV3.unaryMinus() = VectorMath.unaryMinus(this)
operator fun FV3.unaryPlus() = this

operator fun FV3.plus(other: FV3) = VectorMath.plus(this, other)
operator fun FV3.plus(offset: Number) = VectorMath.plus(this, offset)

operator fun FV3.minus(offset: Number) = VectorMath.minus(this, offset)
operator fun FV3.minus(other: FV3) = VectorMath.minus(this, other)

operator fun FV3.times(other: FV3) = VectorMath.times(this, other)
operator fun FV3.times(scale: Number) = VectorMath.times(this, scale)

infix fun FV3.dot(other: FV3) = VectorMath.dot(this, other)
infix fun FV3.cross(other: FV3) = VectorMath.cross(this, other)

fun FV3.unit() = VectorMath.unit(this)

val FV3.sqrLength: Double get() = VectorMath.sqrLength(this)
val FV3.length: Double get() = VectorMath.length(this)

fun FV3.reflect(normal: FV3) = VectorMath.reflect(this, normal)

fun Double.pow(n: Number): Double = VectorMath.pow(this, n)
fun Number.div(other: Double): Double = VectorMath.div(this, other)

fun FV3.toColor() = VectorMath.toColor(this)
fun FV3.unitToColor() = VectorMath.unitToColor(this)
