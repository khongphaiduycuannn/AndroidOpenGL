package com.ndmquan.gl.daynight.wallpaper.core.utils.extensions

import java.nio.FloatBuffer

fun FloatBuffer.updateBuffer(newData: FloatArray) {
    clear()
    put(newData)
    position(0)
}