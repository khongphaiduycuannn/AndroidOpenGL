package com.ndmquan.gl.daynight.wallpaper.core.utils

object RandomUtils {

    fun randomFloat(min: Float, max: Float): Float {
        return (min + (max - min) * Math.random()).toFloat()
    }
}