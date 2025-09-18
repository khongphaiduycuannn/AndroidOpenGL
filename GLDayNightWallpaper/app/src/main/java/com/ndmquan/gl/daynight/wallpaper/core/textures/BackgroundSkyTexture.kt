package com.ndmquan.gl.daynight.wallpaper.core.textures

import android.content.Context
import android.opengl.GLES20
import com.ndmquan.gl.daynight.wallpaper.core.base.Base2DTexture
import com.ndmquan.gl.daynight.wallpaper.core.utils.consts.ByteConstants

abstract class BackgroundSkyTexture(context: Context) : Base2DTexture(context) {

    override fun getNdcSize(
        screenWidth: Int,
        screenHeight: Int
    ): Pair<Float, Float> {
        return 1f to 1f
    }

    override fun getInitialFloatArray(
        ndcWidth: Float,
        ndcHeight: Float
    ): FloatArray {
        return floatArrayOf(
            -1f, -1f, 0f, 0f, 1f,
            1f, -1f, 0f, 1f, 1f,
            -1f, 1f, 0f, 1f, 0f,
            1f, 1f, 0f, 0f, 0f
        )
    }

    override fun onDrawTexture(program: Int) {
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

        val alpha = getAlpha()
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(textureHandle, 0)
        GLES20.glUniform1f(alphaHandle, alpha)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(textureCoordHandle)
    }

    override fun getStrike(): Int {
        return 5 * ByteConstants.FLOAT_BYTES
    }

    abstract fun getAlpha(): Float
}