package com.ndmquan.gl.daynight.wallpaper.core.utils.extensions

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

fun FloatBuffer.updateBuffer(newData: FloatArray): FloatBuffer {
    return if (newData.size > this.capacity()) {
        ByteBuffer.allocateDirect(newData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(newData)
            .apply { position(0) }
    } else {
        clear()
        put(newData)
        position(0)
        this
    }
}