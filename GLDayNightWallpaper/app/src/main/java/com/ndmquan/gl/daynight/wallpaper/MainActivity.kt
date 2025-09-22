package com.ndmquan.gl.daynight.wallpaper

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
import com.ndmquan.gl.daynight.wallpaper.core.DayNightRenderer
import com.ndmquan.gl.daynight.wallpaper.databinding.ActivityMainBinding

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
            Toast.makeText(this@MainActivity, "Set wallpaper", Toast.LENGTH_SHORT).show()
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