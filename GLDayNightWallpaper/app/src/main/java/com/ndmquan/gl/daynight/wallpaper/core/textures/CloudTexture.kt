package com.ndmquan.gl.daynight.wallpaper.core.textures

import android.content.Context
import com.ndmquan.gl.daynight.wallpaper.core.base.Base2DTexture
import com.ndmquan.gl.daynight.wallpaper.core.utils.consts.ByteConstants

class CloudTexture(context: Context) : Base2DTexture(context) {

    private val randomScaleRatio = (500..700).random() / 100f
    private val randomStartXPosition = (-2000..0).random() / 1000f
    private val randomStartYPosition = -1 + 2f * (600..920).random() / 1000f


    override fun getNdcSize(screenWidth: Int, screenHeight: Int): Pair<Float, Float> {
        val widthInPixels = screenWidth / randomScaleRatio
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
        val totalMoveDistance = 4
        val currentMoveDistance = totalMoveDistance * progress

        val currentX = -1.0f + currentMoveDistance - ndcWidth
        val currentY = randomStartYPosition

        return floatArrayOf(
            currentX + randomStartXPosition, currentY, 0f, 0f, 1f,
            currentX + randomStartXPosition + ndcWidth, currentY, 0f, 1f, 1f,
            currentX + randomStartXPosition, currentY + ndcHeight, 0f, 0f, 0f,
            currentX + randomStartXPosition + ndcWidth, currentY + ndcHeight, 0f, 1f, 0f
        )
    }

    override fun getStrike(): Int {
        return 5 * ByteConstants.FLOAT_BYTES
    }
}