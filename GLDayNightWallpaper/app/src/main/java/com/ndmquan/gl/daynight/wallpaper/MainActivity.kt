package com.ndmquan.gl.daynight.wallpaper

import android.Manifest
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
import com.ndmquan.gl.daynight.wallpaper.core.DayNightRenderer
import com.ndmquan.gl.daynight.wallpaper.core.DayNightWallpaperService
import com.ndmquan.gl.daynight.wallpaper.databinding.ActivityMainBinding
import kotlin.jvm.java

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        hideSystemBar()

        initViews()
    }

    private fun initViews() {
        (binding.svPreview.renderer as DayNightRenderer).apply {
            setDuration(10_000L)
        }

        binding.btnSetWallpaper.setOnClickListener {
            PermissionUtils(this).requestPermission(
                Manifest.permission.SET_WALLPAPER,
                SetWallpaperPermissionCallback()
            )
        }
    }


    private fun setLiveWallpaper() {
        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
        val component = ComponentName(applicationContext, DayNightWallpaperService::class.java)
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, component)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun hideSystemBar() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val controller = WindowInsetsControllerCompat(window, v)
            controller.hide(WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior = BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            insets
        }
    }


    inner class SetWallpaperPermissionCallback : PermissionUtils.PermissionCallback {
        override fun onAllGranted(permission: List<String>) {
            setLiveWallpaper()
        }

        override fun onDenied(
            listGranted: List<String>,
            listDenied: List<String>
        ) {

        }

        override fun onShouldOpenSetting(openSettingAction: () -> Unit) {
            openSettingAction.invoke()
        }
    }
}