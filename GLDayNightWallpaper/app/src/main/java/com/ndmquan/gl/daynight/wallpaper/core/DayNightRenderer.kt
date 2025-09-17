package com.ndmquan.gl.daynight.wallpaper.core

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import com.ndmquan.gl.daynight.wallpaper.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class DayNightRenderer(
    private val context: Context
) : GLSurfaceView.Renderer {

    private var screenWidth = 0
    private var screenHeight = 0

    private var program = 0
    private var textureId1 = 0  // Bottom-left texture
    private var textureId2 = 0  // Top-right texture
    private var buffer1: FloatBuffer? = null  // Bottom-left buffer
    private var buffer2: FloatBuffer? = null  // Top-right buffer

    private var positionHandle = 0
    private var textureCoordHandle = 0
    private var textureHandle = 0

    val vertexShader = """
        attribute vec4 a_Position;
        attribute vec2 a_TexCoord;
        varying vec2 v_TexCoord;
        
        void main() {
            gl_Position = a_Position;
            v_TexCoord = a_TexCoord;
        }
    """.trimIndent()

    val fragmentShader = """
        precision mediump float;
        uniform sampler2D u_Texture;
        varying vec2 v_TexCoord;
        
        void main() {
            gl_FragColor = texture2D(u_Texture, v_TexCoord);
        }
    """.trimIndent()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        program = createShaderProgram()
        textureId1 = loadTexture(R.drawable.img_sun)
        textureId2 = loadTexture(R.drawable.img_sun) // Có thể thay bằng texture khác
        setupBuffers()

        positionHandle = GLES20.glGetAttribLocation(program, "a_Position")
        textureCoordHandle = GLES20.glGetAttribLocation(program, "a_TexCoord")
        textureHandle = GLES20.glGetUniformLocation(program, "u_Texture")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        screenWidth = width
        screenHeight = height
        GLES20.glViewport(0, 0, width, height)
        setupBuffers()
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glUseProgram(program)

        val stride = 5 * 4

        // Draw first texture (bottom-left)
        drawTexture(textureId1, buffer1, stride)

        // Draw second texture (top-right)
        drawTexture(textureId2, buffer2, stride)
    }

    private fun drawTexture(textureId: Int, buffer: FloatBuffer?, stride: Int) {
        buffer?.position(0)
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, stride, buffer)

        buffer?.position(3)
        GLES20.glEnableVertexAttribArray(textureCoordHandle)
        GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, stride, buffer)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(textureHandle, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(textureCoordHandle)
    }

    private fun createShaderProgram(): Int {
        val vertexShaderId = loadShader(GLES20.GL_VERTEX_SHADER, vertexShader)
        val fragmentShaderId = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader)

        val program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShaderId)
        GLES20.glAttachShader(program, fragmentShaderId)
        GLES20.glLinkProgram(program)

        return program
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }

    private fun loadTexture(resourceId: Int): Int {
        val textureIds = IntArray(1)
        GLES20.glGenTextures(1, textureIds, 0)

        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0])
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()

        return textureIds[0]
    }

    private fun setupBuffers() {
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.img_sun)
        val textureWidth = bitmap.width
        val textureHeight = bitmap.height
        bitmap.recycle()

        val ndcWidth = (textureWidth.toFloat() / screenWidth) * 2f
        val ndcHeight = (textureHeight.toFloat() / screenHeight) * 2f

        // Bottom-left texture data
        val data1 = floatArrayOf(
            -1.0f, -1.0f, 0f, 0f, 1f,
            -1.0f + ndcWidth * 0.2f, -1.0f, 0f, 1f, 1f,
            -1.0f, -1.0f + ndcHeight * 0.2f, 0f, 0f, 0f,
            -1.0f + ndcWidth * 0.2f, -1.0f + ndcHeight * 0.2f, 0f, 1f, 0f
        )

        // Top-right texture data
        val data2 = floatArrayOf(
            1.0f - ndcWidth * 0.2f, 1.0f - ndcHeight * 0.2f, 0f, 0f, 1f,
            1.0f, 1.0f - ndcHeight * 0.2f, 0f, 1f, 1f,
            1.0f - ndcWidth * 0.2f, 1.0f, 0f, 0f, 0f,
            1.0f, 1.0f, 0f, 1f, 0f
        )

        buffer1 = ByteBuffer.allocateDirect(data1.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(data1)
            .apply { position(0) }

        buffer2 = ByteBuffer.allocateDirect(data2.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(data2)
            .apply { position(0) }
    }
}