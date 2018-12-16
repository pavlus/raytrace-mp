package com.pavlus.raytrace

import com.pavlus.raytrace.model.texture.RasterTexture
import javax.imageio.ImageIO

actual fun loadTexture(name: String): RasterTexture {
    return BufferedImageRasterTexture(
        ImageIO.read(
            BufferedImageRasterTexture::class.java
                .getResourceAsStream(name)
        )
    )
}