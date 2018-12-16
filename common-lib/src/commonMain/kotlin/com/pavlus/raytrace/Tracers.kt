package com.pavlus.raytrace

import com.pavlus.raytrace.model.Camera
import com.pavlus.raytrace.model.Ray
import com.pavlus.raytrace.model.math.nextRandom
import com.pavlus.raytrace.model.math.unit

typealias Tracer = (Camera, Double, Double, Hittable) -> Color


private val BLACK = Color(0, 0, 0)
private fun toColor(ray: Ray, stage: Hittable): Color {
    val hit = stage.getHit(ray, 0.01, Double.MAX_VALUE)
    return hit?.let {
        if (ray.ttl < 0) return BLACK
        val scattered = it.material.scatter(ray, it) ?: return BLACK
        return toColor(scattered, stage) * scattered.atttenuation
    } ?: ray.noHitColor()
}

private fun Ray.noHitColor(): Color {
    val unit = direction.unit()
    val t = (unit.y + 1.0f) * 0.5
    return Color(1, 1, 1) * (1.0 - t) + Color(0.5, 0.7, 1.0) * t
}


fun deterministicTracer(): Tracer =
    { camera: Camera, u: Double, v: Double, stage: Hittable -> color(camera, u, v, stage) }

private fun color(camera: Camera, u: Double, v: Double, stage: Hittable): Color {
    return toColor(camera.getRay(u, v), stage)
}


fun ditheredTracer(
    wStep: Double,
    hStep: Double,
    aaFactor: Int
): Tracer =
    { c, u, v, h -> colorDithered(c, u, v, wStep, hStep, h, aaFactor) }


private fun colorDithered(
    camera: Camera,
    u: Double, v: Double,
    uStep: Double, vStep: Double,
    stage: Hittable, steps: Int
): Color {
    var color = Color(0, 0, 0)
    for (i in 1..steps) {
        color += toColor(camera.getRay((u + nextRandom() * uStep), (v + nextRandom() * vStep)), stage)
    }
    return color * (1.0 / steps)
}
