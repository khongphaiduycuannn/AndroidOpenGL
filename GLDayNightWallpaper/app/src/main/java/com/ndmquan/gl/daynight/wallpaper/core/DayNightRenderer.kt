package com.ndmquan.gl.daynight.wallpaper.core

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.ndmquan.gl.daynight.wallpaper.R
import com.ndmquan.gl.daynight.wallpaper.core.textures.CloudTexture
import com.ndmquan.gl.daynight.wallpaper.core.textures.ColorFilterTexture
import com.ndmquan.gl.daynight.wallpaper.core.textures.LayerDayTexture
import com.ndmquan.gl.daynight.wallpaper.core.textures.LayerNightTexture
import com.ndmquan.gl.daynight.wallpaper.core.textures.MoonTexture
import com.ndmquan.gl.daynight.wallpaper.core.textures.SunTexture
import com.ndmquan.gl.daynight.wallpaper.core.utils.GLLoader
import com.ndmquan.gl.daynight.wallpaper.core.utils.consts.ShaderPatterns
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class DayNightRenderer(
    private val context: Context
) : GLSurfaceView.Renderer {

    companion object {
        const val DEFAULT_DAY_DURATION = 60_000L

        const val MIN_CLOUD_DURATION = 35_000L
        const val MAX_CLOUD_DURATION = 60_000L
        const val CLOUD_COUNT = 7
    }


    private var screenWidth = 0
    private var screenHeight = 0

    private var isPlaying = true
    private var loop = true

    private var dayProgram = 0

    private var lastFrameTime: Long = 0

    private var dayDuration: Long = DEFAULT_DAY_DURATION
    private var dayPosition: Long = 0
    private val animDayProgress
        get() = dayPosition.toFloat() / dayDuration

    private var cloudDuration = mutableListOf<Long>().apply {
        repeat(CLOUD_COUNT) { add((MIN_CLOUD_DURATION..MAX_CLOUD_DURATION).random()) }
    }
    private var cloudPosition = mutableListOf<Long>().apply {
        repeat(CLOUD_COUNT) { add(0) }
    }
    private val animCloudProgress
        get() = cloudPosition.mapIndexed { index, value -> value.toFloat() / cloudDuration[index] }


    private val sunTexture = SunTexture(context)
    private val moonTexture = MoonTexture(context)
    private val cloudTextures = mutableListOf<CloudTexture>()
        .apply { repeat(CLOUD_COUNT) { add(CloudTexture(context)) } }

    private val layerDayTexture = LayerDayTexture(context)
    private val layerNightTexture = LayerNightTexture(context)
    private val foregroundDayTexture = LayerDayTexture(context)
    private val foregroundNightTexture = LayerNightTexture(context)
    private val colorFilterTexture = ColorFilterTexture(context)


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        dayProgram = createShaderProgram()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        val sizeChanged = screenWidth != width || screenHeight != height
        screenWidth = width
        screenHeight = height
        GLES20.glViewport(0, 0, width, height)

        if (sizeChanged) {
            initTexture()
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glUseProgram(dayProgram)

        drawTexture()

        val deltaTime = System.currentTimeMillis() - lastFrameTime
        if (deltaTime < 200) {
            dayPosition += deltaTime
            cloudPosition.forEachIndexed { index, value ->
                cloudPosition[index] = value + deltaTime
            }
            when {
                loop && dayPosition >= dayDuration -> dayPosition = 0
                !loop && dayPosition >= dayDuration -> isPlaying = false
            }
            if (loop) {
                cloudPosition.forEachIndexed { index, it ->
                    if (it > cloudDuration[index]) cloudPosition[index] = 0
                }
            }
        }
        if (isPlaying) {
            lastFrameTime = System.currentTimeMillis()
        }
    }


    private fun createShaderProgram(): Int {
        val vertexShaderId = GLLoader.loadShader(
            GLES20.GL_VERTEX_SHADER, ShaderPatterns.Texture2D.vertex
        )
        val fragmentShaderId = GLLoader.loadShader(
            GLES20.GL_FRAGMENT_SHADER, ShaderPatterns.Texture2D.fragment
        )

        val program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShaderId)
        GLES20.glAttachShader(program, fragmentShaderId)
        GLES20.glLinkProgram(program)

        return program
    }

    private fun initTexture() {
        sunTexture.init(
            dayProgram, R.drawable.img_sun, screenWidth, screenHeight
        )
        moonTexture.init(
            dayProgram, R.drawable.img_moon, screenWidth, screenHeight
        )
        cloudTextures.forEach {
            it.init(
                dayProgram, R.drawable.img_cloud, screenWidth, screenHeight
            )
        }
        foregroundDayTexture.init(
            dayProgram, R.drawable.img_foreground_bright, screenWidth, screenHeight
        )
        foregroundNightTexture.init(
            dayProgram, R.drawable.img_foreground_night, screenWidth, screenHeight
        )
        layerDayTexture.init(
            dayProgram, R.drawable.img_sky_bright, screenWidth, screenHeight
        )
        layerNightTexture.init(
            dayProgram, R.drawable.img_sky_night, screenWidth, screenHeight
        )
        colorFilterTexture.init(
            dayProgram, screenWidth, screenHeight
        )
    }

    private fun drawTexture() {
        layerNightTexture.animProgress = animDayProgress
        layerNightTexture.drawTexture(dayProgram)

        layerDayTexture.animProgress = animDayProgress
        layerDayTexture.drawTexture(dayProgram)

        sunTexture.animProgress = animDayProgress
        sunTexture.drawTexture(dayProgram)

        moonTexture.animProgress = animDayProgress
        moonTexture.drawTexture(dayProgram)

        foregroundNightTexture.animProgress = animDayProgress
        foregroundNightTexture.drawTexture(dayProgram)

        foregroundDayTexture.animProgress = animDayProgress
        foregroundDayTexture.drawTexture(dayProgram)

        cloudTextures.forEachIndexed { index, it ->
            it.animProgress = animCloudProgress[index]
            it.drawTexture(dayProgram)
        }

        colorFilterTexture.animProgress = animDayProgress
        colorFilterTexture.drawTexture(dayProgram)
    }


    fun getDuration(): Long = this.dayDuration

    fun isPlaying(): Boolean = this.isPlaying

    fun isLoop(): Boolean = this.loop

    fun setDuration(duration: Long) {
        this.dayDuration = duration
    }

    fun play() {
        this.isPlaying = true
    }

    fun pause() {
        this.isPlaying = false
    }

    fun setLoop(loop: Boolean) {
        this.loop = loop
    }
}