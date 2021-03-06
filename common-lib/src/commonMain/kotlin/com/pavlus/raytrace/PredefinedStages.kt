package com.pavlus.raytrace

import com.pavlus.raytrace.model.FV3
import com.pavlus.raytrace.model.hittable.*
import com.pavlus.raytrace.model.material.*
import com.pavlus.raytrace.model.math.*
import com.pavlus.raytrace.model.texture.CheckerTexture
import com.pavlus.raytrace.model.texture.ColorTexture
import com.pavlus.raytrace.model.texture.ImageTexture
import com.pavlus.raytrace.model.texture.MarbleTexture


fun texturedStage(texture: String): Hittable {
    return Stage(
        listOf(
            Sphere(Point(0, 0, 0), 2, Lambertian(ImageTexture(loadTexture(texture))))
        )
    )
}

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

fun lightedStage(): Hittable {
    val perlin = Perlin()
    val texture = MarbleTexture(perlin, 1)
    return Stage(
        listOf(
            Sphere(Point(0, -1003, 0), 1000, Lambertian(texture)),
            Sphere(Point(-2, -1, -1), 2, Lambertian(texture)),
            Sphere(Point(-2, 4, 0), 2, DiffuseLight(ColorTexture(4, 0, 4))),
            XYRect(1, 3, -2, 0, -2, DiffuseLight(ColorTexture(0, 4, 0)))
        )
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
                    list.add(
                        MovingSphere(
                            center, center1, 0.0, 1.0, 0.2,
                            Lambertian(lrnd, lrnd, lrnd)
                        )
                    )
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
    return BvhNode(list, 0, 1)
}


fun generateMovingStageWithTexture(w: IntRange = -10 until 10, h: IntRange = -6 until 6): Hittable {
    val list = ArrayList<Hittable>()
    fun add(center: Point, radius: Number, material: Material) {
        list.add(Sphere(center, radius, material))
    }

    fun add(x: Number, y: Number, z: Number, radius: Number, material: Material) {
        list.add(Sphere(Point(x, y, z), radius, material))
    }
    add(
        0, -1000, 0, 1000,
        Lambertian(
            CheckerTexture(
                ColorTexture(0.2, 0.3, 0.1),
                ColorTexture(0.9, 0.9, 0.9)
            )
        )
    )
    val Q = FV3(4, 0.2, 0)
    for (a in w) {
        for (b in h) {
            val cmat = rnd
            val center = FV3(a + 0.9 * rnd, 0.2, b + 0.9 * rnd)
            if ((center - Q).length > 0.9) {
                if (cmat < 0.8) { //diffuse
                    val center1 = center + Point(0, 0.5 * rnd, 0)
                    list.add(
                        MovingSphere(
                            center, center1, 0.0, 1.0, 0.2,
                            Lambertian(lrnd, lrnd, lrnd)
                        )
                    )
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
    add(8, 1, 0.6, 1, Lambertian(MarbleTexture(Perlin(), 1)))
    return BvhNode(list, 0.0, 1.0)
}

fun boxCornell(): Hittable {
    val red = Lambertian(0.65, 0.05, 0.05)
    val white = Lambertian(0.73, 0.73, 0.73)
    val green = Lambertian(0.12, 0.45, 0.15)
    val blue = Lambertian(0.05, 0.05, 0.85)
    val yellow = Lambertian(0.65, 0.65, 0.05)
    val light = DiffuseLight(32, 32, 32)
    val sizePlus = 0
    return BvhNode(
        listOf(
            YZRect(0, 555, 0, 555, 555, green).invertNormal(),
            YZRect(0, 555, 0, 555, 0, red),
            XZRect(0, 555, 0, 555, 555, white).invertNormal() /*ceiling*/,
            XZRect(213-sizePlus, 343+sizePlus, 227-sizePlus, 332+sizePlus, 554, light).invertNormal(),
            XZRect(0, 555, 0, 555, 0, white), /*floor*/
            XYRect(0, 555, 0, 555, 555, white).invertNormal(), /*back wall*/
            Box(Point(265,0,295), Point(430,330,460), white),
            Box(Point(130,0,65), Point(295,165,230), white)
        ), 0,1
    )
}

private val rnd: Double
    get() = nextRandom()

private val lrnd: Double
    get() = rnd * rnd
private val mrnd: Double
    get() = 0.5 * (1 + rnd)
