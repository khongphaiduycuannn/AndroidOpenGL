package com.ndmquan.gl.daynight.wallpaper.core.data

import kotlin.math.floor

class ColorFilterInterpolator(private val colorFilters: List<ColorFilter>) {
    
    init {
        require(colorFilters.isNotEmpty()) { "ColorFilter list cannot be empty" }
    }

    private var lastProgress: Float = -1f
    private var cachedFilter: ColorFilter? = null


    fun getInterpolatedFilter(progress: Float): ColorFilter {
        val clampedProgress = progress.coerceIn(0f, 1f)
        
        if (clampedProgress == lastProgress && cachedFilter != null) {
            return cachedFilter!!
        }

        val result = when {
            colorFilters.size == 1 -> colorFilters[0]
            clampedProgress == 0f -> colorFilters.first()
            clampedProgress == 1f -> colorFilters.last()
            else -> interpolateAtProgress(clampedProgress)
        }

            lastProgress = clampedProgress
            cachedFilter = result

        return result
    }

    private fun interpolateAtProgress(progress: Float): ColorFilter {
        val n = colorFilters.size
        val segmentSize = 1f / (n - 1)
        val segmentIndex = floor(progress / segmentSize).toInt().coerceIn(0, n - 2)
        
        val segmentStart = segmentIndex * segmentSize
        val segmentEnd = (segmentIndex + 1) * segmentSize
        
        val t = if (segmentEnd == segmentStart) 0f
                else ((progress - segmentStart) / (segmentEnd - segmentStart)).coerceIn(0f, 1f)
        
        val fromFilter = colorFilters[segmentIndex]
        val toFilter = colorFilters[segmentIndex + 1]
        
        return interpolateBetweenFilters(fromFilter, toFilter, t)
    }

    private fun interpolateBetweenFilters(from: ColorFilter, to: ColorFilter, t: Float): ColorFilter {
        return ColorFilter(
            topColor = lerpColorArray(from.topColor, to.topColor, t),
            centerColor = lerpColorArray(from.centerColor, to.centerColor, t),
            bottomColor = lerpColorArray(from.bottomColor, to.bottomColor, t),
            intensity = lerp(from.intensity, to.intensity, t)
        )
    }

    private fun lerpColorArray(from: FloatArray, to: FloatArray, t: Float): FloatArray {
        require(from.size == to.size) { "Color arrays must have same size" }
        return FloatArray(from.size) { i ->
            lerp(from[i], to[i], t).coerceIn(0f, 1f)
        }
    }

    private fun lerp(a: Float, b: Float, t: Float): Float = a + (b - a) * t


    fun getFilterByProgress(progress: Float): ColorFilter {
        return getInterpolatedFilter(progress)
    }
}