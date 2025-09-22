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
            uniform float u_Alpha;
            
            uniform vec4 u_TopColor;
            uniform vec4 u_CenterColor;
            uniform vec4 u_BottomColor;
            uniform float u_FilterIntensity;
            
            varying vec2 v_TexCoord;
            
            void main() {
                vec4 originalColor = texture2D(u_Texture, v_TexCoord);
                
                float gradientFactor = v_TexCoord.y; 
                
                vec4 filterColor;
                if (gradientFactor < 0.5) {
                    float t = gradientFactor * 2.0; 
                    filterColor = mix(u_BottomColor, u_CenterColor, t);
                } else {
                    float t = (gradientFactor - 0.5) * 2.0; 
                    filterColor = mix(u_CenterColor, u_TopColor, t);
                }
                
                vec4 filteredColor = mix(originalColor, originalColor * filterColor, u_FilterIntensity);
                
                gl_FragColor = filteredColor * u_Alpha;
            }
        """.trimIndent()
    }
}