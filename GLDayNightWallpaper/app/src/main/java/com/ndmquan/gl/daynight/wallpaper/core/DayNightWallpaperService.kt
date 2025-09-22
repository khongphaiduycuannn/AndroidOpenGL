package com.ndmquan.gl.daynight.wallpaper.core

import android.opengl.GLES20
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import kotlinx.coroutines.*
import java.util.concurrent.Executors
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay
import javax.microedition.khronos.egl.EGLSurface

class DayNightWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine = WallpaperEngine()

    inner class WallpaperEngine : Engine() {

        private var renderer: DayNightRenderer? = null
        @Volatile private var isVisible = false

        private var surfaceWidth: Int = 0
        private var surfaceHeight: Int = 0

        private val renderingDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        private val scope = CoroutineScope(SupervisorJob() + renderingDispatcher)
        private var renderJob: Job? = null

        private var egl: EGL10? = null
        private var eglDisplay: EGLDisplay? = null
        private var eglConfig: EGLConfig? = null
        private var eglContext: EGLContext? = null
        private var eglSurface: EGLSurface? = null

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            renderer = DayNightRenderer(applicationContext)
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            surfaceWidth = width
            surfaceHeight = height
            stopRendering()
            startRendering(holder)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            stopRendering()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            isVisible = visible
            if (visible) {
                renderer?.play()
            } else {
                renderer?.pause()
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            stopRendering()
            scope.cancel()
            renderingDispatcher.close()
            renderer = null
        }

        private fun startRendering(holder: SurfaceHolder) {
            if (renderJob?.isActive == true || surfaceWidth == 0 || surfaceHeight == 0) return

            renderJob = scope.launch {
                if (initEGL(holder)) {
                    renderer?.onSurfaceCreated(null, null)
                    renderer?.onSurfaceChanged(null, surfaceWidth, surfaceHeight)

                    while (isActive) {
                        if (isVisible) {
                            renderer?.onDrawFrame(null)
                            egl?.eglSwapBuffers(eglDisplay, eglSurface)
                            delay(16)
                        } else {
                            delay(100)
                        }
                    }
                }
                cleanupEGL()
            }
        }

        private fun stopRendering() {
            runBlocking {
                renderJob?.cancelAndJoin()
            }
            renderJob = null
        }

        private fun initEGL(holder: SurfaceHolder): Boolean {
            try {
                egl = EGLContext.getEGL() as EGL10
                eglDisplay = egl!!.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)

                val version = IntArray(2)
                if (!egl!!.eglInitialize(eglDisplay, version)) return false

                val configSpec = intArrayOf(
                    EGL10.EGL_RENDERABLE_TYPE, 4,
                    EGL10.EGL_RED_SIZE, 8,
                    EGL10.EGL_GREEN_SIZE, 8,
                    EGL10.EGL_BLUE_SIZE, 8,
                    EGL10.EGL_ALPHA_SIZE, 8,
                    EGL10.EGL_DEPTH_SIZE, 16,
                    EGL10.EGL_STENCIL_SIZE, 0,
                    EGL10.EGL_NONE
                )

                val configs = arrayOfNulls<EGLConfig>(1)
                val numConfigs = IntArray(1)
                if (!egl!!.eglChooseConfig(eglDisplay, configSpec, configs, 1, numConfigs)) return false
                if (numConfigs[0] <= 0) return false

                eglConfig = configs[0]

                val contextAttrs = intArrayOf(0x3098, 2, EGL10.EGL_NONE)
                eglContext = egl!!.eglCreateContext(eglDisplay, eglConfig, EGL10.EGL_NO_CONTEXT, contextAttrs)
                if (eglContext == EGL10.EGL_NO_CONTEXT) return false

                eglSurface = egl!!.eglCreateWindowSurface(eglDisplay, eglConfig, holder, null)
                if (eglSurface == EGL10.EGL_NO_SURFACE) return false

                if (!egl!!.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) return false

                return true
            } catch (e: Exception) {
                cleanupEGL()
                return false
            }
        }

        private fun cleanupEGL() {
            try {
                egl?.let { egl ->
                    if (eglDisplay != null) {
                        egl.eglMakeCurrent(eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT)
                        eglSurface?.let { egl.eglDestroySurface(eglDisplay, it) }
                        eglContext?.let { egl.eglDestroyContext(eglDisplay, it) }
                        egl.eglTerminate(eglDisplay)
                    }
                }
            } catch (_: Exception) { }

            egl = null
            eglDisplay = null
            eglConfig = null
            eglContext = null
            eglSurface = null
        }
    }
}