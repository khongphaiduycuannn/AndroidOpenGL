package com.ndmquan.gl.daynight.wallpaper.core

import android.content.Context
import android.util.AttributeSet
import com.ndmquan.gl.daynight.wallpaper.core.base.BaseSurfaceView

class DayNightSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : BaseSurfaceView(context, attrs) {

    override fun createRenderer(): Renderer {
        return DayNightRenderer(context)
    }
}