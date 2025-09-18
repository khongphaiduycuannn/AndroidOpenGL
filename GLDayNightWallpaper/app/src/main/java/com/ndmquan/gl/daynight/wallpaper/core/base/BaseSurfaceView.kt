package com.ndmquan.gl.daynight.wallpaper.core.base

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

abstract class BaseSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs) {

    val renderer: Renderer

    abstract fun createRenderer(): Renderer

    init {
        setEGLContextClientVersion(2)
        renderer = createRenderer()
        setRenderer(renderer)
    }
}