package com.ndmquan.gl.daynight.wallpaper.core.data

data class ColorFilter(
    val topColor: FloatArray,
    val centerColor: FloatArray,
    val bottomColor: FloatArray,
    val intensity: Float
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ColorFilter

        if (!topColor.contentEquals(other.topColor)) return false
        if (!centerColor.contentEquals(other.centerColor)) return false
        if (!bottomColor.contentEquals(other.bottomColor)) return false
        if (intensity != other.intensity) return false

        return true
    }

    override fun hashCode(): Int {
        var result = topColor.contentHashCode()
        result = 31 * result + centerColor.contentHashCode()
        result = 31 * result + bottomColor.contentHashCode()
        result = 31 * result + intensity.hashCode()
        return result
    }
}