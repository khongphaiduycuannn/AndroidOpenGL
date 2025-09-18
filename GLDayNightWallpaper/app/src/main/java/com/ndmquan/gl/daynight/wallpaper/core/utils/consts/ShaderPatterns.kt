package com.ndmquan.gl.daynight.wallpaper.core.utils.consts

object ShaderPatterns {

    object Texture2D {

        val vertex = """
            attribute vec4 a_Position;
            attribute vec2 a_TexCoord;
            varying vec2 v_TexCoord;
            
            void main() {
                gl_Position = a_Position;
                v_TexCoord = a_TexCoord;
            }
        """.trimIndent()

        val fragment = """
            precision mediump float;
            uniform sampler2D u_Texture;
            varying vec2 v_TexCoord;
            uniform float u_Alpha;
            
            void main() {
                gl_FragColor = texture2D(u_Texture, v_TexCoord) * u_Alpha;
            }
        """.trimIndent()
    }
}