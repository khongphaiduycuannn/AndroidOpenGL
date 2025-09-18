package com.ndmquan.gl.daynight.wallpaper.core

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.ndmquan.gl.daynight.wallpaper.R
import com.ndmquan.gl.daynight.wallpaper.core.textures.CloudTexture
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
        const val DEFAULT_DAY_DURATION = 10000L

        const val MIN_CLOUD_DURATION = 25000L
        const val MAX_CLOUD_DURATION = 40000L
        const val CLOUD_COUNT = 7
    }


    private var screenWidth = 0
    private var screenHeight = 0

    private var isPlaying = true
    private var loop = true

    private var positionHandle = 0
    private var textureCoordHandle = 0
    private var textureHandle = 0

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


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        dayProgram = createShaderProgram()

        positionHandle = GLES20.glGetAttribLocation(dayProgram, "a_Position")
        textureCoordHandle = GLES20.glGetAttribLocation(dayProgram, "a_TexCoord")
        textureHandle = GLES20.glGetUniformLocation(dayProgram, "u_Texture")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        screenWidth = width
        screenHeight = height
        GLES20.glViewport(0, 0, width, height)

        initTexture()
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glUseProgram(dayProgram)

        drawTexture()

        val deltaTime = System.currentTimeMillis() - lastFrameTime
        if (deltaTime < 30) {
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
        sunTexture.init(R.drawable.img_sun, screenWidth, screenHeight)
        moonTexture.init(R.drawable.img_moon, screenWidth, screenHeight)
        cloudTextures.forEach { it.init(R.drawable.img_cloud, screenWidth, screenHeight) }
    }

    private fun drawTexture() {
        sunTexture.animProgress = animDayProgress
        sunTexture.drawTexture(positionHandle, textureCoordHandle, textureHandle)

        moonTexture.animProgress = animDayProgress
        moonTexture.drawTexture(positionHandle, textureCoordHandle, textureHandle)

        cloudTextures.forEachIndexed { index, it ->
            it.animProgress = animCloudProgress[index]
            it.drawTexture(positionHandle, textureCoordHandle, textureHandle)
        }
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