package com.ndmquan.gl.daynight.wallpaper.core.textures

import android.content.Context

class LayerDayTexture(context: Context) : LayerTexture(context) {

    override fun getAlpha(): Float {
        return when {
            0f <= animProgress && animProgress <= 0.2 -> animProgress * 5
            0.2 < animProgress && animProgress < 0.6 -> 1f
            0.6 <= animProgress && animProgress <= 0.8 -> 1f - (animProgress - 0.6f) * 5
            else -> 0f
        }
    }
}