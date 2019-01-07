package com.pavlus.raytrace.model

import com.pavlus.raytrace.Hittable
import com.pavlus.raytrace.RenderTarget
import com.pavlus.raytrace.Tracer

data class Sweep(
    val x0: Int, val x1: Int, val wStep: Double,
    val y0: Int, val y1: Int, val hStep: Double
) {
    fun render(tracer: Tracer, camera: Camera, stage: Hittable, target: RenderTarget) {
        var v = y0 * hStep
        for (y in y0 until y1) {
//            println("y: $y0..$y1")
            v += hStep
            var u = x0 * wStep
            for (x in x0 until x1) {
                //                println("x: $x0..$x1")
                u += wStep
                val color = tracer.invoke(camera, u, v, stage)
                target.put(x, y, color)
            }
        }
    }
}