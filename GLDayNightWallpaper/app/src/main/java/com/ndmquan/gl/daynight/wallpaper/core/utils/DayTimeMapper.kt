package com.ndmquan.gl.daynight.wallpaper.core.utils

object DayTimeMapper {

    /**
     * Progress: 0 -> 1
     * Time: start from 6h00 -> 6h00 + 24h
     * */

    fun getDayStartProgress(): Float {
        return 0.0f
    }

    fun getDayEndProgress(): Float {
        return 0.5f
    }


    fun getSunRiseProgress(progress: Float): Pair<Float, Float> {
        return 0.9f to 0.1f
    }

    fun isSunRise(progress: Float): Boolean {
        return progress >= getSunRiseProgress(progress).first
                || progress <= getSunRiseProgress(progress).second
    }


    fun getSunSetProgress(progress: Float): Pair<Float, Float> {
        return 0.5f to 0.7f
    }

    fun isSunSet(progress: Float): Boolean {
        return progress >= getSunSetProgress(progress).first
                && progress <= getSunSetProgress(progress).second
    }


    fun getProgressDiff(a: Float, b: Float): Float {
        val diff = a - b
        return if (diff < 0) {
            diff + 1
        } else {
            diff
        }
    }
}