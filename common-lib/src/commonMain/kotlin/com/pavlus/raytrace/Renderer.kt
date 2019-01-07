package com.pavlus.raytrace

import com.pavlus.raytrace.model.Camera
import com.pavlus.raytrace.model.Sweep
import kotlin.math.min

class Renderer(val camera: Camera, val stage: Hittable, val tracer: Tracer) {

    fun renderTo(target: RenderTarget, parallelism: Int, executor: (() -> Unit) -> Unit, allSubmited: () -> Unit = {}) {
//        println(stage)

        val h = target.height
        val w = target.width
        val hStep = 1.0 / h
        val wStep = 1.0 / w

        val horizontals = ArrayList<Pair<Int, Int>>()
        val verticals = ArrayList<Pair<Int, Int>>()

        val renderPart = 64
        part(h, renderPart, horizontals)
        part(w, renderPart, verticals)
        horizontals
            .flatMap { (y0, y1) -> verticals.map { (x0, x1) -> Sweep(x0, x1, wStep, y0, y1, hStep) } }
            .shuffled(randomizer)
            .forEach {
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

