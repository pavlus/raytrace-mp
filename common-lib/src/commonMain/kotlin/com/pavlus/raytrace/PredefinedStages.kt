package com.pavlus.raytrace

import com.pavlus.raytrace.model.*
import com.pavlus.raytrace.model.hittable.BvhNode
import com.pavlus.raytrace.model.hittable.MovingSphere
import com.pavlus.raytrace.model.hittable.Sphere
import com.pavlus.raytrace.model.hittable.Stage
import com.pavlus.raytrace.model.math.*
import com.pavlus.raytrace.model.texture.CheckerTexture
import com.pavlus.raytrace.model.texture.ColorTexture
import com.pavlus.raytrace.model.texture.MarbleTexture

fun simpleStage(): Hittable {
    return BvhNode(
        listOf(
            Sphere(Point(0, -100.5, 1), 100, Lambertian(0.8, 0.8, 0.0)),
            Sphere(Point(1, 0, -1), 0.5, Metal(0.8, 0.6, 0.2, 1.0)),
            Sphere(Point(-1, 0, -1), 0.5, Dielectric(1.5)),
            Sphere(Point(0, 0, -1), 0.5, Lambertian(0.1, 0.2, 0.5))
//            Sphere(Point(-1, 0, -1), -0.5, Dielectric(1.5))
        ),
        0, 0
    )
}


fun noiseStage(): Hittable {
    val perlin = Perlin()
    val texture = MarbleTexture(perlin, 1)
    return BvhNode(
        listOf(
            Sphere(Point(0, -1000, 0), 1000, Lambertian(texture)),
            Sphere(Point(0, 2, 0), 2, Lambertian(texture))
        ),
        0, 0
    )
}


fun generateStaticStage(w: Int = 11, h: Int = 11): Hittable {
    val list = ArrayList<Hittable>()
    fun add(center: Point, radius: Number, material: Material) {
        list.add(Sphere(center, radius, material))
    }

    fun add(x: Number, y: Number, z: Number, radius: Number, material: Material) {
        list.add(Sphere(Point(x, y, z), radius, material))
    }
    add(0, -1000, 0, 1000, Lambertian(0.5, 0.5, 0.5))
    val Q = FV3(4, 0.2, 0)
    for (a in -w until w) {
        for (b in -h until h) {
            val cmat = rnd
            val center = FV3(a + 0.9 * rnd, 0.2, b + 0.9 * rnd)
            if ((center - Q).length > 0.9) {
                if (cmat < 0.8) { //diffuse
                    add(center, 0.2, Lambertian(lrnd, lrnd, lrnd))
                } else if (cmat < 0.95) {//metal
                    add(center, 0.2, Metal(mrnd, mrnd, mrnd, mrnd))
                } else { // glass
                    add(center, 0.2, Dielectric(1.5))
                }
            }
        }
    }
    add(0, 1, 0, 1, Dielectric(1.5))
    add(-4, 1, 0, 1, Lambertian(0.4, 0.2, 0.1))
    add(4, 1, 0, 1, Metal(0.7, 0.6, 0.5, 0.0))
    return BvhNode(list, 0, 0)
}


fun generateMovingStage(w: Int = 10, h: Int = 10): Hittable {
    val list = ArrayList<Hittable>()
    fun add(center: Point, radius: Number, material: Material) {
        list.add(Sphere(center, radius, material))
    }

    fun add(x: Number, y: Number, z: Number, radius: Number, material: Material) {
        list.add(Sphere(Point(x, y, z), radius, material))
    }
    add(0, -1000, 0, 1000, Lambertian(0.5, 0.5, 0.5))
    val Q = FV3(4, 0.2, 0)
    for (a in -w until w) {
        for (b in -h until h) {
            val cmat = rnd
            val center = FV3(a + 0.9 * rnd, 0.2, b + 0.9 * rnd)
            if ((center - Q).length > 0.9) {
                if (cmat < 0.8) { //diffuse
                    val center1 = center + Point(0, 0.5 * rnd, 0)
                    list.add(MovingSphere(center, center1, 0.0, 1.0, 0.2, Lambertian(lrnd, lrnd, lrnd)))
                } else if (cmat < 0.95) {//metal
                    add(center, 0.2, Metal(mrnd, mrnd, mrnd, mrnd))
                } else { // glass
                    add(center, 0.2, Dielectric(1.5))
                }
            }
        }
    }
    add(0, 1, 0, 1, Dielectric(1.5))
    add(-4, 1, 0, 1, Lambertian(0.4, 0.2, 0.1))
    add(4, 1, 0, 1, Metal(0.7, 0.6, 0.5, 0.0))
    return Stage(list)
}



fun generateMovingStageWithTexture(w: Int = 10, h: Int = 10): Hittable {
    val list = ArrayList<Hittable>()
    fun add(center: Point, radius: Number, material: Material) {
        list.add(Sphere(center, radius, material))
    }

    fun add(x: Number, y: Number, z: Number, radius: Number, material: Material) {
        list.add(Sphere(Point(x, y, z), radius, material))
    }
    add(0, -1000, 0, 1000, Lambertian(CheckerTexture(ColorTexture(0.2, 0.3, 0.1), ColorTexture(0.9,0.9,0.9))))
    val Q = FV3(4, 0.2, 0)
    for (a in -w until w) {
        for (b in -h until h) {
            val cmat = rnd
            val center = FV3(a + 0.9 * rnd, 0.2, b + 0.9 * rnd)
            if ((center - Q).length > 0.9) {
                if (cmat < 0.8) { //diffuse
                    val center1 = center + Point(0, 0.5 * rnd, 0)
                    list.add(MovingSphere(center, center1, 0.0, 1.0, 0.2, Lambertian(lrnd, lrnd, lrnd)))
                } else if (cmat < 0.95) {//metal
                    add(center, 0.2, Metal(mrnd, mrnd, mrnd, mrnd))
                } else { // glass
                    add(center, 0.2, Dielectric(1.5))
                }
            }
        }
    }
    add(-4, 1, 0.6, 1, Lambertian(0.4, 0.2, 0.1))
    add(0, 1, 0.6, 1, Dielectric(1.5))
    add(4, 1, 0.6, 1, Metal(0.7, 0.6, 0.5, 0.0))
    add(8, 1, 0.6, 1, Lambertian(MarbleTexture(Perlin(), 100)))
    return Stage(list)
}



private val rnd: Double
    get() = nextRandom()

private val lrnd: Double
    get() = rnd * rnd
private val mrnd: Double
    get() = 0.5 * (1 + rnd)
