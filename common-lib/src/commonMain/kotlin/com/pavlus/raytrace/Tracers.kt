package com.pavlus.raytrace

import com.pavlus.raytrace.model.Camera
import com.pavlus.raytrace.model.Ray
import com.pavlus.raytrace.model.math.nextRandom
import kotlin.math.max

typealias Tracer = (Camera, Double, Double, Hittable) -> Color


private val BLACK = Color(0, 0, 0)
private val GRAY = Color(0.5, 0.5, 0.5)
private fun toColor(ray: Ray, stage: Hittable): Color {
    return stage.getHit(ray, 0.01, Double.MAX_VALUE)?.let {
        val (u, v) = it.uv
        val emmited = it.material.emmit(u, v, it.point)
        val scattered = it.material.scatter(ray, it)
        if (ray.ttl < 0 || scattered == null) return@let emmited
        val attenuated = toColor(scattered, stage) * scattered.atttenuation
        return if (emmited == null) attenuated else emmited + attenuated
    } ?: ray.noHitColor()
}

private fun Ray.noHitColor(): Color {
    return BLACK
    /*
    val unit = direction.unit()
    val t = (unit.y + 1.0f) * 0.5
    return Color(1, 1, 1) * (1.0 - t) + Color(0.5, 0.7, 1.0) * t*/
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


fun overExposureCompensationTracer(delegate: Tracer): Tracer = { camera, u, v, hittable ->
    val original =  delegate(camera, u, v, hittable)
    compensateExposure(original)
}

private fun compensateExposure(original: Color): Color {
    val (r, g, b) = original
    val max = max(r, max(g, b))
    return if (max > 1) Color(r / max, g / max, b / max)
    else Color(
        r.coerceIn(0.0, 1.0),
        g.coerceIn(0.0, 1.0),
        b.coerceIn(0.0, 1.0)
    )
}