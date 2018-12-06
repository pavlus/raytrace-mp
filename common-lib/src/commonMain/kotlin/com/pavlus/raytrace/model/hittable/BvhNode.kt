package com.pavlus.raytrace.model.hittable

import com.pavlus.raytrace.Hittable
import com.pavlus.raytrace.model.Hit
import com.pavlus.raytrace.model.Ray
import com.pavlus.raytrace.model.math.Point
import com.pavlus.raytrace.model.randomizer

// fixme this implementation is probably the bug source if you added new primitives
class BvhNode(
    private val children: Collection<Hittable>,
    time0: Number, time1: Number
) : Hittable {

    val right: Hittable?
    val left: Hittable?

    private val bounds: Aabb?

    private val axis: Int = randomizer.nextInt(0, 3)


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
        lbox ?: throw NullPointerException("Null bounding box in BvhNode onstructor")
        rbox ?: throw NullPointerException("Null bounding box in BvhNode onstructor")
        bounds = Aabb.surrounding(lbox, rbox)
        this.left = left
        this.right = right
    }

    private class HittableComparator(val axis: Int) : Comparator<Hittable> {
        override fun compare(a: Hittable, b: Hittable): Int {
            val left = a.boundingBox(0, 0)!!
            val right = b.boundingBox(0, 0)!!
            val diff = left.center[axis] - right.center[axis]
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