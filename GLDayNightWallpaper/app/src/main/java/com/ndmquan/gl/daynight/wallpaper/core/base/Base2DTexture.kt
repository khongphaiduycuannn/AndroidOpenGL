package com.ndmquan.gl.daynight.wallpaper.core.base

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import com.ndmquan.gl.daynight.wallpaper.core.utils.GLLoader
import com.ndmquan.gl.daynight.wallpaper.core.utils.consts.ByteConstants
import com.ndmquan.gl.daynight.wallpaper.core.utils.extensions.updateBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

abstract class Base2DTexture(
    private val context: Context
) {
    protected var textureId: Int = 0

    protected var buffer: FloatBuffer? = null

    protected var screenWidth: Int = 0
    protected var screenHeight: Int = 0
    protected var textureWidth: Float = 0f
    protected var textureHeight: Float = 0f
    protected var ndcWidth: Float = 0f
    protected var ndcHeight: Float = 0f

    var animProgress: Float = 0f

    fun init(
        resourceId: Int, screenWidth: Int, screenHeight: Int
    ) {
        textureId = GLLoader.loadTexture(context, resourceId)
        this.screenWidth = screenWidth
        this.screenHeight = screenHeight

        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId)
        textureWidth = bitmap.width.toFloat()
        textureHeight = bitmap.height.toFloat()
        bitmap.recycle()

        val size = getNdcSize(screenWidth, screenHeight)
        ndcWidth = size.first
        ndcHeight = size.second

        val data = getInitialFloatArray(ndcWidth, ndcHeight)
        buffer = ByteBuffer.allocateDirect(data.size * ByteConstants.FLOAT_BYTES)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(data)
            .apply { position(0) }
    }

    abstract fun getNdcSize(screenWidth: Int, screenHeight: Int): Pair<Float, Float>
    abstract fun getInitialFloatArray(ndcWidth: Float, ndcHeight: Float): FloatArray
    open fun getAnimFloatArray(progress: Float, ndcWidth: Float, ndcHeight: Float): FloatArray {
        return getInitialFloatArray(ndcWidth, ndcHeight)
    }
    abstract fun getStrike(): Int

    open fun onDrawTexture(positionHandle: Int, textureCoordHandle: Int, textureHandle: Int) {
        buffer?.position(0)
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, getStrike(), buffer)

        buffer?.position(3)
        GLES20.glEnableVertexAttribArray(textureCoordHandle)
        GLES20.glVertexAttribPointer(
            textureCoordHandle,
            2,
            GLES20.GL_FLOAT,
            false,
            getStrike(),
            buffer
        )

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(textureHandle, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(textureCoordHandle)
    }

    fun drawTexture(positionHandle: Int, textureCoordHandle: Int, textureHandle: Int) {
        if (animProgress != 0f) {
            val data = getAnimFloatArray(animProgress, ndcWidth, ndcHeight)
            buffer = buffer?.updateBuffer(data)
        }
        onDrawTexture(positionHandle, textureCoordHandle, textureHandle)
    }
}