package com.pavlus.raytrace.model.hittable

import com.pavlus.raytrace.*
import com.pavlus.raytrace.model.Hit
import com.pavlus.raytrace.model.Ray
import com.pavlus.raytrace.model.math.Point
import com.pavlus.raytrace.model.math.minus
import com.pavlus.raytrace.model.randomizer

class Stage(private val children: Collection<Hittable>) : Hittable {

    override fun getHit(ray: Ray, minDist: Number, maxDist: Number): Hit? {
        var hit: Hit? = null
        var closest: Double = maxDist.toDouble()
        val min = minDist.toDouble()
        children.forEach {
            it.getHit(ray, min, closest)?.let {
                hit = it
                closest = it.distance
            }
        }
        return hit
    }


    override fun boundingBox(time0: Number, time1: Number): Aabb? {
        return aabb(children, time0, time1)
    }

}


fun aabb(children: Collection<Hittable>, time0: Number, time1: Number): Aabb? {
    return children.mapNotNull {
        it.boundingBox(time0, time1)
    }.ifEmpty {
        null
    }?.reduce(Aabb.Companion::surrounding)
}


// fixme this implementation is probably the bug source if you added new primitives
class BvhNode(
    private val children: Collection<Hittable>,
    time0: Number, time1: Number
) : Hittable {

    val right: Hittable?
    val left: Hittable?

    private val bounds: Aabb?

    private val axis: (Point) -> Double = when (randomizer.nextInt(0, 3)) {
        0 -> Point::x
        1 -> Point::y
        else -> Point::z
    }

    init {
        val left: Hittable
        val right: Hittable
        when (children.size) {
            0, 1 -> {
                left = children.first()
                right = children.first()
            }
            2 -> {
                left = children.first()
                right = children.last()
            }
            else -> {
//                val sorted = children.sortedBy{ it.boundingBox(time0, time1)?.min?.let { corner -> axis(corner) } }
                val sorted = children.sortedWith(HittableComparator(axis))
                val halfLen = sorted.size / 2
                left = BvhNode(sorted.subList(0, halfLen), time0, time1)
                right = BvhNode(sorted.subList(halfLen, sorted.size), time0, time1)

            }
        }
        val lbox = left.boundingBox(time0, time1)
        val rbox = right.boundingBox(time0, time1)
        checkNotNull(lbox) { "Null bounding box in BvhNode onstructor" }
        checkNotNull(rbox) { "Null bounding box in BvhNode onstructor" }
        bounds = Aabb.surrounding(lbox, rbox)
        this.left = left
        this.right = right
    }

    private class HittableComparator(val axis: (Point) -> Double) : Comparator<Hittable> {
        override fun compare(a: Hittable, b: Hittable): Int {
            val left = a.boundingBox(0, 0)!!
            val right = b.boundingBox(0, 0)!!
            val diff = axis(left.center) - axis(right.center)
            val result = when{
                diff<0.0 -> -1
                diff>0.0 -> 1
                else -> 0
            }
            return result
        }
    }

    override fun getHit(ray: Ray, minDistance: Number, maxDistance: Number): Hit? {
        if (bounds?.hit(ray, minDistance, maxDistance) != true) return null
        val leftHit = left?.getHit(ray, minDistance, maxDistance)
        val rightHit = right?.getHit(ray, minDistance, maxDistance)

        return if (leftHit != null && rightHit != null) {
            if (leftHit.distance < rightHit.distance) leftHit
            else rightHit
        } else leftHit ?: rightHit
    }

    override fun boundingBox(time0: Number, time1: Number): Aabb? = bounds
    override fun toString(): String {
        return "BvhNode(children=$children, right=$right, left=$left, bounds=$bounds, axis=$axis)"
    }


}