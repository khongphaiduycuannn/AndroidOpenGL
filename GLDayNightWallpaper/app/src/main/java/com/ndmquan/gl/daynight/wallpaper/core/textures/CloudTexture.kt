package com.ndmquan.gl.daynight.wallpaper.core.textures

import android.content.Context
import com.ndmquan.gl.daynight.wallpaper.core.base.Base2DTexture
import com.ndmquan.gl.daynight.wallpaper.core.utils.RandomUtils
import com.ndmquan.gl.daynight.wallpaper.core.utils.consts.ByteConstants

class CloudTexture(context: Context) : Base2DTexture(context) {

    private val randomScaleRatio = RandomUtils.randomFloat(5f, 7f)
    private val randomStartXPosition = RandomUtils.randomFloat(-2f, 0f)
    private val randomStartYPosition = -1 + 2f * RandomUtils.randomFloat(0.7f, 0.92f)


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