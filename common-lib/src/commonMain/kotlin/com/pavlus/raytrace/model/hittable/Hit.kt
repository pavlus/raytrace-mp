package com.pavlus.raytrace.model

import com.pavlus.raytrace.Hittable
import com.pavlus.raytrace.model.material.Material
import com.pavlus.raytrace.model.math.Point

data class Hit(
    val distance: Double,
    val point: Point,
    val normal: FV3,
    val material: Material,
    val uv: Pair<Double, Double>,
    val hittable: Hittable?
)