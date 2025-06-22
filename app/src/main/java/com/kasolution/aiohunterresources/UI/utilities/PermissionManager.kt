package com.kasolution.aiohunterresources.UI.utilities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionManager(private val activity: Activity) {

    private var callback: ((Boolean) -> Unit)? = null

    fun setPermissionCallback(callback: (Boolean) -> Unit) {
        this.callback = callback
    }

    fun solicitarPermisos(permisos: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, permisos, requestCode)
    }

    fun verificarPermiso(permiso: String): Boolean {
        val resultado = ContextCompat.checkSelfPermission(activity, permiso)
        return resultado == PackageManager.PERMISSION_GRANTED
    }

    fun verificarPermisoInstalar(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity.packageManager.canRequestPackageInstalls()
        } else {
            true
        }
    }

    fun solicitarPermisoInstalar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                data = Uri.parse("package:${activity.packageName}")
            }
            activity.startActivity(intent)
        }
    }

    fun onRequestPermissionsResult(grantResults: IntArray) {
        val permisosConcedidos = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        callback?.invoke(permisosConcedidos)
    }
}
