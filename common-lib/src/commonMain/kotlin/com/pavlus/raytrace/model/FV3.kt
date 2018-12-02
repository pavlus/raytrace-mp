package com.pavlus.raytrace.model


data class FV3(val x: Double, val y: Double, val z: Double) {
    constructor(x: Number, y: Number, z: Number) : this(x.toDouble(), y.toDouble(), z.toDouble())
    operator fun get(i:Int)=when(i){
        0->x
        1->y
        2->z
        else -> throw IndexOutOfBoundsException("$i is out of range [0..2]")
    }
}