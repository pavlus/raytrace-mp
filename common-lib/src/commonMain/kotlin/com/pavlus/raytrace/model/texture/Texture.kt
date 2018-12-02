package com.pavlus.raytrace.model.texture

import com.pavlus.raytrace.Color
import com.pavlus.raytrace.model.FV3

interface Texture {
    fun color(u: Number, v: Number, position: FV3): Color
}

