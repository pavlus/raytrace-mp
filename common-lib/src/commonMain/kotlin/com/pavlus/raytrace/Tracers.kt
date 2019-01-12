package com.pavlus.raytrace

import com.pavlus.raytrace.model.Camera
import com.pavlus.raytrace.model.Ray
import com.pavlus.raytrace.model.math.nextRandom
import kotlin.math.max

private val GRAY = Color(0.5, 0.5, 0.5)
private val BLACK = Color(0, 0, 0)

interface Tracer {
    fun invoke(camera: Camera, u: Double, v: Double, stage: Hittable, initialHitable: Hittable? = null): Color
    fun noHit(ray: Ray): Color = GRAY

    fun trace(ray: Ray, stage: Hittable?, initialHitable: Hittable? = null): Color {
        val hit = initialHitable?.getHit(ray, 0.01, Double.MAX_VALUE)
            ?: stage?.getHit(ray, 0.01, Double.MAX_VALUE)
            ?: return noHit(ray)

        val emitted = hit.material.emmit(hit.uv, hit.point)

        val scattered = hit.material.scatter(ray, hit) ?: return emitted ?: noHit(ray)
        if (ray.ttl < 0) return noHit(ray)

        val attenuated = trace(scattered, stage) * ray.atttenuation
        if (emitted == null) return attenuated
        return emitted + attenuated

    }
}


class RayTracer(val ambientLight: (Ray) -> Color = { GRAY }) : Tracer {
    override fun invoke(camera: Camera, u: Double, v: Double, stage: Hittable, initialHitable: Hittable?): Color {
        return trace(camera.ray(u, v), stage, initialHitable)
    }

    override fun noHit(ray: Ray): Color = ambientLight(ray)
}


class PathTracer(
    val wStep: Double,
    val hStep: Double,
    val aaFactor: Int,
    val ambientLight: (Ray) -> Color = { GRAY }
) : Tracer {
    override fun invoke(camera: Camera, u: Double, v: Double, stage: Hittable, initialHitable: Hittable?): Color {
        var color = Color(0, 0, 0)
        for (i in 1..aaFactor) {
            color += trace(camera.ray(u + nextRandom() * wStep, v + nextRandom() * hStep), stage)
        }
        return color * (1.0 / aaFactor)

    }

    override fun noHit(ray: Ray) = ambientLight(ray)

}

class ExposureCompensationTracer(val delegate: Tracer) : Tracer by delegate {
    override fun invoke(camera: Camera, u: Double, v: Double, stage: Hittable, initialHitable: Hittable?): Color {
        val original = delegate.invoke(camera, u, v, stage, initialHitable)
        val (r, g, b) = original
        val max = max(r, max(g, b))
        return Color(
            r.coerceIn(0.0, 1.0),
            g.coerceIn(0.0, 1.0),
            b.coerceIn(0.0, 1.0)
        )
    }
}

