package com.pavlus.raytrace

import com.pavlus.raytrace.model.Camera
import kotlin.math.min

class Renderer(val camera: Camera, val stage: Hittable, val tracer:Tracer) {

    fun renderTo(target: RenderTarget, parallelism: Int, executor: (() -> Unit) -> Unit, allSubmited: () -> Unit = {}) {
//        println(stage)

        val h = target.height
        val w = target.width
        val hStep = 1.0 / h
        val wStep = 1.0 / w

        val horizontals = ArrayList<Pair<Int, Int>>()
        val verticals = ArrayList<Pair<Int, Int>>()

        part(h, 32, horizontals)
        part(w, 32, verticals)
        horizontals
            .flatMap { (y0, y1) ->
                verticals.map { (x0, x1) ->
                    Sweep(x0, x1, wStep, y0, y1, hStep)
                }
            }.shuffled().forEach {
                executor { it.render(tracer, camera, stage, target) }
            }
        executor(allSubmited)
    }

    private fun part(
        h: Int,
        renderPart: Int,
        horizontals: ArrayList<Pair<Int, Int>>
    ) {
        var y0 = 0
        var y1 = 0
        while (y1 < h) {
            y1 = min(y1 + renderPart, h)
            horizontals.add(Pair(y0, y1))
            y0 = y1
        }
    }


}

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
                val color = tracer(camera, u, v, stage)
                target.put(x, y, color)
            }
        }
    }
}