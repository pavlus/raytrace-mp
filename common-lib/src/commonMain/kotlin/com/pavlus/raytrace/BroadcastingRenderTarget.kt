package com.pavlus.raytrace

class BroadcastingRenderTarget(val subscribers: Collection<RenderTarget>)
    : RenderTarget() {

    override val height: Int = subscribers.minBy(RenderTarget::height)?.height ?: 0
    override val width: Int = subscribers.minBy(RenderTarget::width)?.width ?: 0

    override fun put(x: Int, y: Int, color: Color) {
        subscribers.forEach {
            it.put(x, y, color)
        }
    }
}