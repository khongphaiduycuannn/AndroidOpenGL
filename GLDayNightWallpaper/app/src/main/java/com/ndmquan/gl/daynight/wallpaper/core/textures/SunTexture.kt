package com.ndmquan.gl.daynight.wallpaper.core.textures

import android.content.Context
import com.ndmquan.gl.daynight.wallpaper.core.base.Base2DTexture
import com.ndmquan.gl.daynight.wallpaper.core.utils.consts.ByteConstants

class SunTexture(context: Context) : Base2DTexture(context) {

    override fun getNdcSize(
        screenWidth: Int,
        screenHeight: Int
    ): Pair<Float, Float> {
        val widthInPixels = screenWidth / 4f
        val heightInPixels = widthInPixels * textureHeight / textureWidth

        val ndcWidth = (widthInPixels / screenWidth) * 2f
        val ndcHeight = (heightInPixels / screenHeight) * 2f
        return ndcWidth to ndcHeight
    }

    override fun getInitialFloatArray(ndcWidth: Float, ndcHeight: Float): FloatArray {
        return floatArrayOf(
            -1.0f, -1.0f, 0f, 0f, 1f,
            -1.0f, -1.0f, 0f, 1f, 1f,
            -1.0f, -1.0f, 0f, 0f, 0f,
            -1.0f, -1.0f, 0f, 1f, 0f
        )
    }

    override fun getAnimFloatArray(progress: Float, ndcWidth: Float, ndcHeight: Float): FloatArray {
        if (progress > 0.5f) {
            return getInitialFloatArray(ndcWidth, ndcHeight)
        }

        val progress = progress * 2

        val totalMoveDistance = 2 + ndcWidth
        val currentMoveDistance = totalMoveDistance * progress
        val currentX = -1.0f + currentMoveDistance - ndcWidth

        val parabolHeight = -1f * (progress - 0.5f) * (progress - 0.5f) + 0.25f
        val maxParabolHeight = 1.2f
        val currentY = 0.35f + parabolHeight * maxParabolHeight

        return floatArrayOf(
            currentX, currentY, 0f, 0f, 1f,
            currentX + ndcWidth, currentY, 0f, 1f, 1f,
            currentX, currentY + ndcHeight, 0f, 0f, 0f,
            currentX + ndcWidth, currentY + ndcHeight, 0f, 1f, 0f
        )
    }

    override fun getStrike(): Int {
        return 5 * ByteConstants.FLOAT_BYTES
    }
}