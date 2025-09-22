package com.ndmquan.gl.daynight.wallpaper.core.base

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import com.ndmquan.gl.daynight.wallpaper.core.utils.GLLoader
import com.ndmquan.gl.daynight.wallpaper.core.utils.GLLoader.createEmptyTexture
import com.ndmquan.gl.daynight.wallpaper.core.utils.consts.ByteConstants
import com.ndmquan.gl.daynight.wallpaper.core.utils.extensions.updateBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

abstract class Base2DTexture(
    private val context: Context
) {
    protected var textureId: Int = 0

    protected var positionHandle: Int = 0
    protected var textureCoordHandle: Int = 0
    protected var textureHandle: Int = 0
    protected var alphaHandle: Int = 0

    protected var buffer: FloatBuffer? = null

    protected var screenWidth: Int = 0
    protected var screenHeight: Int = 0
    protected var textureWidth: Float = 0f
    protected var textureHeight: Float = 0f
    protected var ndcWidth: Float = 0f
    protected var ndcHeight: Float = 0f


    private var lastAnimProgress: Float = -1f
    var animProgress: Float = 0f


    fun init(program: Int, resourceId: Int, screenWidth: Int, screenHeight: Int) {
        textureId = GLLoader.createResourceTexture(context, resourceId)

        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId)
        textureWidth = bitmap.width.toFloat()
        textureHeight = bitmap.height.toFloat()
        bitmap.recycle()

        initCommon(program, screenWidth, screenHeight)
    }

    fun init(program: Int, screenWidth: Int, screenHeight: Int) {
        textureId = createEmptyTexture()
        textureWidth = screenWidth.toFloat()
        textureHeight = screenHeight.toFloat()

        initCommon(program, screenWidth, screenHeight)
    }


    private fun initCommon(program: Int, screenWidth: Int, screenHeight: Int) {
        this.screenWidth = screenWidth
        this.screenHeight = screenHeight

        val size = getNdcSize(screenWidth, screenHeight)
        ndcWidth = size.first
        ndcHeight = size.second

        val data = getInitialFloatArray(ndcWidth, ndcHeight)
        buffer = ByteBuffer.allocateDirect(data.size * ByteConstants.FLOAT_BYTES)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(data)
            .apply { position(0) }

        cacheHandles(program)
    }

    abstract fun getNdcSize(screenWidth: Int, screenHeight: Int): Pair<Float, Float>
    abstract fun getInitialFloatArray(ndcWidth: Float, ndcHeight: Float): FloatArray
    open fun getAnimFloatArray(progress: Float, ndcWidth: Float, ndcHeight: Float): FloatArray {
        return getInitialFloatArray(ndcWidth, ndcHeight)
    }
    abstract fun getStrike(): Int

    open fun onDrawTexture(program: Int) {
        buffer?.position(0)
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(
            positionHandle, 3, GLES20.GL_FLOAT, false, getStrike(), buffer
        )

        buffer?.position(3)
        GLES20.glEnableVertexAttribArray(textureCoordHandle)
        GLES20.glVertexAttribPointer(
            textureCoordHandle, 2, GLES20.GL_FLOAT, false, getStrike(), buffer
        )

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(textureHandle, 0)
        GLES20.glUniform1f(alphaHandle, 1f)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(textureCoordHandle)
    }

    fun drawTexture(program: Int) {
        if (animProgress != 0f && shouldUpdateBuffer()) {
            val data = getAnimFloatArray(animProgress, ndcWidth, ndcHeight)
            buffer?.updateBuffer(data)
        }
        onDrawTexture(program)
    }


    private fun shouldUpdateBuffer(): Boolean {
        val shouldUpdate = lastAnimProgress != animProgress
        lastAnimProgress = animProgress
        return shouldUpdate
    }

    private fun cacheHandles(program: Int) {
        positionHandle = GLES20.glGetAttribLocation(program, "a_Position")
        textureCoordHandle = GLES20.glGetAttribLocation(program, "a_TexCoord")
        textureHandle = GLES20.glGetUniformLocation(program, "u_Texture")
        alphaHandle = GLES20.glGetUniformLocation(program, "u_Alpha")
    }
}