package com.pavlus.raytrace

import com.pavlus.raytrace.model.Camera
import com.pavlus.raytrace.model.FV3
import javafx.animation.AnimationTimer
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.geometry.Orientation
import javafx.scene.Parent
import javafx.scene.canvas.Canvas
import javafx.scene.image.WritableImage
import tornadofx.*
import java.io.File
import java.nio.file.Paths
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import javax.imageio.ImageIO

class ViewRoot : View("My View") {
    private var c: Canvas? = null
    val parallelism = Runtime.getRuntime().availableProcessors() + 1
    private val es = Executors.newWorkStealingPool(parallelism)

    private val aaProperty = SimpleIntegerProperty(32)
    val aa by aaProperty

    private val ttlProperty = SimpleIntegerProperty(16)
    val ttl by ttlProperty

    private val targetWidthProperty = SimpleIntegerProperty(400)
    val targetWidth by targetWidthProperty

    private val targetHeightProperty = SimpleIntegerProperty(200)
    val targetHeight by targetHeightProperty

    private val fovProperty = SimpleDoubleProperty(20.0)
    val fov by fovProperty


    private val runningProperty = SimpleBooleanProperty(false)
    private var running by runningProperty
    private var stage = getStage()
    private lateinit var container: Parent

    private var startTime = 0L

    override val root = borderpane {
        top {
            form {
                disableWhen(runningProperty)
                fieldset(labelPosition = Orientation.HORIZONTAL) {
                    hbox(8) {
                        field("AA factor: ") {
                            spinner(1, 4096, aa, 1, true, aaProperty)
                        }

                        field("Ray TTL: ") {
                            spinner(1, 128, ttl, 1, true, ttlProperty)
                        }
                    }
                }
                fieldset(labelPosition = Orientation.HORIZONTAL) {
                    hbox(8) {
                        field("Width: ") {
                            spinner(1, 8096, targetWidth, 4, true, targetWidthProperty)
                        }

                        field("Height: ") {
                            spinner(1, 8096, targetHeight, 4, true, targetHeightProperty)
                        }
                    }
                }
            }
        }
        center {
            container = scrollpane {
            }
        }
        bottom {
            buttonbar {
                disableWhen(runningProperty)
                button("Render") {
                    action(::doRender)
                }
                button("Clear") {
                    action(::clearCanvas)
                }
                button("Recreate stage") {
                    action { stage = getStage() }
                }
            }
        }
//        doRender()
    }

    private fun clearCanvas() {
        c?.let {
            it.graphicsContext2D?.clearRect(0.0, 0.0, it.width, it.height)
        }
    }

    private lateinit var started: LocalDateTime
    private fun doRender() {
        running = true
        startTime = System.currentTimeMillis()
        val last: Canvas? = c
        val current: Canvas
        if (last == null || targetWidth.toDouble() != last.width || targetHeight.toDouble() != last.height) {
            last?.removeFromParent()
            current = Canvas(targetWidth.toDouble(), targetHeight.toDouble())
            container.add(current)
            c = current
        } else {
            current = last
        }

        val w = current.width.toInt()
        val h = current.height.toInt()
        val aa = aa

        val img = WritableImage(w, h)
        val screenTarget = PWRenderTarget(w, h, img.pixelWriter)
        val bufferedTarget = BufferedImageRenderTarget(w, h)

        val screenDrawer = animationTimer(img, frameTime = 1000)

        val counter = SubmissionCounter()

        val executor: (() -> Unit) -> Unit = {
            counter.submited()
            es.submit {
                it.invoke()
                val done = counter.done()

                println("Done $done / ${counter.total}")
            }
        }

        val WHITE = Color(1, 1, 1)
        val renderer = Renderer(
            camera = getCamera(),
            stage = stage,
            tracer = ExposureCompensationTracer(
                PathTracer(1.0 / current.width, 1.0 / current.height, aa, { WHITE })
            )
        )

        started = LocalDateTime.now()
        println("Starting renderer at $started")
        renderer.renderTo(
            BroadcastingRenderTarget(listOf(screenTarget, bufferedTarget)),
            parallelism + 2, executor,
            cleaner(counter, screenDrawer, bufferedTarget)
        )
        screenDrawer.start()
    }

    private fun cleaner(
        taskCount: SubmissionCounter,
        screenDrawer: AnimationTimer,
        bufferedTarget: BufferedImageRenderTarget
    ): () -> Unit {
        return {
            var finalizingTask: Runnable? = null
            finalizingTask = Runnable {
                if (taskCount.left > 0) {
                    Thread.sleep(taskCount.left * 100L)
                    es.submit(finalizingTask)
                    Thread.yield()
                } else {
                    running = false
                    val now = LocalDateTime.now()
                    val duration = Duration.between(started, now)
                    println("Finished rendering at at $now, in total rendert took $duration")
                    val time = System.currentTimeMillis() - startTime
                    println("Frame took $time ms")
                    screenDrawer.stop()
                    val file = generateFile(time)
                    println("Saving to: $file")
                    ImageIO.write(bufferedTarget.buffer, "png", file)
                }

            }
            es.submit(finalizingTask)
        }
    }

    private fun generateFile(timeSpent: Long): File {
        val now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MMM-dd-HH_mm_ss"))
        val aa = aa
        val ttl = ttl
        val w = targetWidth
        val h = targetHeight
        return Paths.get("${w}x$h-AA$aa-rttl$ttl-${timeSpent}ms-$now.png").toFile()
    }

    private fun animationTimer(img: WritableImage, frameTime: Int = 500): AnimationTimer {
        return object : AnimationTimer() {
            private var lastTime = 0L
            private val targetFrameTime = frameTime
            override fun handle(now: Long) {
                if (now > lastTime) {
                    c?.graphicsContext2D?.drawImage(img, 0.0, 0.0)
                    lastTime = now + targetFrameTime
                }
            }
        }
    }


    private fun getStage(): Hittable {
//        return simpleStage()
//        return generateStaticStage(w = 11, h = 11)
//        return generateMovingStage(w = 10, h = 10)
        return generateMovingStageWithTexture(w = -20..7, h = 0..5)
//        return texturedStage("/earthmap1k.jpg")
//        return lightedStage()
//        return noiseStage()
    }

    private fun getCamera(): Camera {
        val from = FV3(13, 2, 3)
        val at = FV3(0, 0, 0)
        return Camera.camera(
            from = from,
            at = at,
            vup = FV3(0, -1, 0),
            vFov = fov,
            aspect = c!!.width / c!!.height,
            focusDistance = 10.0,
            aperture = 0.0,
            rayTTL = ttl,
            t0 = 0.45,
            t1 = 0.6
        )
    }

    data class SubmissionCounter(
        private val submited: AtomicInteger = AtomicInteger(0),
        private val done: AtomicInteger = AtomicInteger(0)
    ) {
        fun submited() = submited.incrementAndGet()


        fun done() = done.incrementAndGet()


        val left get() = submited.get() - done.get()
        val total get() = submited.get()

    }

}

