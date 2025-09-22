package com.ndmquan.gl.daynight.wallpaper

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import java.util.UUID

fun AppCompatActivity.isPermissionGranted(permission: String): Boolean {
    return when (permission) {
        Manifest.permission.SYSTEM_ALERT_WINDOW -> {
            Settings.canDrawOverlays(this)
        }

        Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE -> {
            val packageName = packageName
            val flat = Settings.Secure.getString(
                contentResolver,
                "enabled_notification_listeners"
            )
            flat?.contains(packageName) == true
        }

        else -> {
            ActivityCompat.checkSelfPermission(this, permission) == 0
        }
    }
}

fun AppCompatActivity.shouldShowPermissionRationale(permission: String): Boolean {
    return ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
}

fun AppCompatActivity.openSettingPermission() {
    val intent = Intent("android.settings.APPLICATION_DETAILS_SETTINGS")
    val uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    startActivity(intent)
}

fun AppCompatActivity.openSettingPermission(permission: String) {
    when (permission) {
        Manifest.permission.SYSTEM_ALERT_WINDOW -> {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            val uri = "package:$packageName".toUri()
            intent.data = uri
            startActivity(intent)
        }

        Manifest.permission.POST_NOTIFICATIONS -> {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            startActivity(intent)
        }

        Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE -> {
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            startActivity(intent)
        }

        else -> openSettingPermission()
    }
}

class PermissionUtils(
    private val activity: AppCompatActivity
) {

    interface PermissionCallback {
        fun onAllGranted(permission: List<String>)
        fun onDenied(listGranted: List<String>, listDenied: List<String>)
        fun onShouldOpenSetting(openSettingAction: () -> Unit)
    }


    private var permissions: List<String> = listOf()

    private val isPermissionGranted: Boolean
        get() = permissions.all { activity.isPermissionGranted(it) }

    private val isShouldShowPermissionRationale: Boolean
        get() = permissions.any { activity.shouldShowPermissionRationale(it) }


    fun requestPermission(
        permission: String,
        callback: PermissionCallback,
    ) {
        requestPermissions(
            listOf(permission),
            callback
        )
    }

    fun requestPermissions(
        permissions: List<String>,
        callback: PermissionCallback
    ) {
        this.permissions = permissions

        if (isGrantAll()) {
            callback.onAllGranted(permissions)
            return
        }

        val contract = ActivityResultContracts.RequestMultiplePermissions()
        activity.activityResultRegistry
            .register("requestPermission_${UUID.randomUUID()}", contract) { newResult ->
                if (isGrantAll()) {
                    callback.onAllGranted(permissions)
                    return@register
                }

                val granted = newResult.filter { it.value }.keys.toList()
                val denied = newResult.filter { !it.value }.keys.toList()

                callback.onDenied(granted, denied)

                if (!isShouldShowPermissionRationale) {
                    callback.onShouldOpenSetting(openSettingAction = {
                        if (denied.size > 1) {
                            activity.openSettingPermission()
                        } else if (denied.size == 1) {
                            activity.openSettingPermission(denied.first())
                        }
                    })
                }
            }
            .launch(permissions.toTypedArray())
    }


    private fun isGrantAll(): Boolean {
        return isPermissionGranted
    }
}