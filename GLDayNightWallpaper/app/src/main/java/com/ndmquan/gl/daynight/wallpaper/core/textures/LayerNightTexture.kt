package com.ndmquan.gl.daynight.wallpaper.core.textures

import android.content.Context
import com.ndmquan.gl.daynight.wallpaper.core.utils.DayTimeMapper

class LayerNightTexture(context: Context) : LayerTexture(context) {

    override fun getAlpha(): Float {
        val sunRise = DayTimeMapper.getSunRiseProgress(animProgress)
        val sunSet = DayTimeMapper.getSunSetProgress(animProgress)
        return 1 - when {
            DayTimeMapper.isSunRise(animProgress) ->
                DayTimeMapper.getProgressDiff(
                    animProgress, sunRise.first
                ) / DayTimeMapper.getProgressDiff(
                    sunRise.second, sunRise.first
                )

            sunRise.second < animProgress && animProgress < sunSet.first -> 1f

            DayTimeMapper.isSunSet(animProgress) ->
                1f - DayTimeMapper.getProgressDiff(
                    animProgress, sunSet.first
                ) / DayTimeMapper.getProgressDiff(
                    sunSet.second, sunSet.first
                )

            else -> 0f
        }
    }
}