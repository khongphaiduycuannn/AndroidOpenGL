package com.ndmquan.gl.daynight.wallpaper

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
import com.ndmquan.gl.daynight.wallpaper.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        hideSystemBar()
    }


    private fun hideSystemBar() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val controller = WindowInsetsControllerCompat(window, v)
            controller.hide(WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior = BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            insets
        }
    }
}