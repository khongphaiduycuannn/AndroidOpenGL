package com.ndmquan.gl.daynight.wallpaper.core.data

object FilterData {

    private const val PALE_INTENSITY = 0.2f

    val sunriseFilter = ColorFilter(
        centerColor = floatArrayOf(1.0f, 0.75f, 0.65f, 1f),
        topColor = floatArrayOf(0.95f, 0.85f, 0.80f, 1f),
        bottomColor = floatArrayOf(1.0f, 0.65f, 0.40f, 1f),
        intensity = PALE_INTENSITY
    )

    val midDayFilter = ColorFilter(
        centerColor = floatArrayOf(1.0f, 0.95f, 0.60f, 1f),
        topColor = floatArrayOf(0.98f, 0.98f, 0.80f, 1f),
        bottomColor = floatArrayOf(1.0f, 0.90f, 0.45f, 1f),
        intensity = PALE_INTENSITY
    )

    val sunsetFilter = ColorFilter(
        centerColor = floatArrayOf(1.0f, 0.60f, 0.20f, 1f),
        topColor = floatArrayOf(1.0f, 0.75f, 0.40f, 1f),
        bottomColor = floatArrayOf(0.95f, 0.45f, 0.15f, 1f),
        intensity = PALE_INTENSITY
    )

    val nightFilter = ColorFilter(
        centerColor = floatArrayOf(0.15f, 0.25f, 0.60f, 1f),
        topColor = floatArrayOf(0.20f, 0.35f, 0.75f, 1f),
        bottomColor = floatArrayOf(0.10f, 0.15f, 0.45f, 1f),
        intensity = PALE_INTENSITY
    )

    val filters = listOf(sunriseFilter, midDayFilter, sunsetFilter, nightFilter)
}