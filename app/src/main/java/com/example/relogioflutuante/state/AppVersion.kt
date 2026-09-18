package com.example.relogioflutuante.state

import android.content.Context
import android.os.Build

private fun packageInfo(context: Context) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    context.packageManager.getPackageInfo(
        context.packageName,
        android.content.pm.PackageManager.PackageInfoFlags.of(0L)
    )
} else {
    @Suppress("DEPRECATION")
    context.packageManager.getPackageInfo(context.packageName, 0)
}

fun appVersionName(context: Context): String = packageInfo(context).versionName ?: "—"

fun appVersionCode(context: Context): Long = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
    packageInfo(context).longVersionCode
} else {
    @Suppress("DEPRECATION")
    packageInfo(context).versionCode.toLong()
}
