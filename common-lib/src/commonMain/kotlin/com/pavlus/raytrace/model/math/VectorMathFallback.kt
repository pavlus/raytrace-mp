package com.pavlus.raytrace.model.math

import com.pavlus.raytrace.Color
import com.pavlus.raytrace.model.FV3
import kotlin.math.pow
import kotlin.math.sqrt

object VectorMathFallback : IVectorMath {

    override fun unaryMinus(self: FV3) = FV3(-self.x, -self.y, -self.z)

    override fun plus(self: FV3, other: FV3) = FV3(self.x + other.x, self.y + other.y, self.z + other.z)


    override fun plus(self: FV3, offset: Number): FV3 {
        val value = offset.toDouble()
        return FV3(self.x + value, self.y + value, self.z + value)
    }

    override fun minus(self: FV3, offset: Number): FV3 {
        val value = offset.toDouble()
        return FV3(self.x - value, self.y - value, self.z - value)
    }

    override fun minus(self: FV3, other: FV3) =
        FV3(self.x - other.x, self.y - other.y, self.z - other.z)

    override fun times(self: FV3, other: FV3) =
        FV3(self.x * other.x, self.y * other.y, self.z * other.z)

    override fun times(self: FV3, scale: Number): FV3 {
        val factor = scale.toDouble()
        return FV3(self.x * factor, self.y * factor, self.z * factor)
    }

    override fun dot(self: FV3, other: FV3) = self.x * other.x + self.y * other.y + self.z * other.z

    override fun cross(self: FV3, other: FV3) = FV3(
        self.y * other.z - self.z * other.y,
        self.z * other.x - self.x * other.z,
        self.x * other.y - self.y * other.x
    )

    override fun unit(self: FV3): FV3 {
        val len = self.length
        return FV3(self.x / len, self.y / len, self.z / len)
    }

    override fun sqrLength(self: FV3): Double = self.x * self.x + self.y * self.y + self.z * self.z
    override fun length(self: FV3): Double = sqrt(self.sqrLength)
    override fun toColor(self: FV3) = Color(self.x, self.y, self.z)
    override fun unitToColor(self: FV3) = Color(0.5 + 0.5 * self.x, 0.5 + 0.5 * self.y, 0.5 + 0.5 * self.z)

    override fun reflect(self: FV3, normal: FV3): FV3 {
        return self - normal * dot(self, normal) * 2
    }

    override fun pow(fl: Double, n: Number): Double = fl.pow(n.toDouble())
    override fun div(number: Number, other: Double): Double = number.toDouble().div(other)
}