package com.ndmquan.gl.daynight.wallpaper.core.textures

import android.content.Context
import android.opengl.GLES20
import android.util.Log
import com.ndmquan.gl.daynight.wallpaper.core.base.Base2DTexture
import com.ndmquan.gl.daynight.wallpaper.core.data.ColorFilter
import com.ndmquan.gl.daynight.wallpaper.core.data.ColorFilterInterpolator
import com.ndmquan.gl.daynight.wallpaper.core.data.FilterData.filters
import com.ndmquan.gl.daynight.wallpaper.core.utils.consts.ByteConstants

class ColorFilterTexture(
    private val context: Context
) : Base2DTexture(context) {

    private val colorFilterInterpolator = ColorFilterInterpolator(filters)


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

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)

        val progress = animProgress
        val filter = colorFilterInterpolator.getInterpolatedFilter(progress)

        applyFilterUniforms(program, filter)

        GLES20.glUniform1i(textureHandle, 0)
        GLES20.glUniform1f(alphaHandle, 1f)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(textureCoordHandle)
    }

    private fun applyFilterUniforms(program: Int, filter: ColorFilter) {
        val topColorHandle = GLES20.glGetUniformLocation(program, "u_TopColor")
        val centerColorHandle = GLES20.glGetUniformLocation(program, "u_CenterColor")
        val bottomColorHandle = GLES20.glGetUniformLocation(program, "u_BottomColor")
        val intensityHandle = GLES20.glGetUniformLocation(program, "u_FilterIntensity")

        if (topColorHandle != -1) {
            GLES20.glUniform4fv(topColorHandle, 1, filter.topColor, 0)
        }
        if (centerColorHandle != -1) {
            GLES20.glUniform4fv(centerColorHandle, 1, filter.centerColor, 0)
        }
        if (bottomColorHandle != -1) {
            GLES20.glUniform4fv(bottomColorHandle, 1, filter.bottomColor, 0)
        }
        if (intensityHandle != -1) {
            GLES20.glUniform1f(intensityHandle, filter.intensity)
        }
    }

    override fun getNdcSize(screenWidth: Int, screenHeight: Int): Pair<Float, Float> {
        return 1f to 1f
    }

    override fun getInitialFloatArray(ndcWidth: Float, ndcHeight: Float): FloatArray {
        return floatArrayOf(
            -1f, -1f, 0f,   0f, 1f,
            1f, -1f, 0f,   1f, 1f,
            -1f,  1f, 0f,   0f, 0f,
            1f,  1f, 0f,   1f, 0f
        )
    }

    override fun getStrike(): Int {
        return 5 * ByteConstants.FLOAT_BYTES
    }
}