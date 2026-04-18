package com.chang.nf.android.data.model

import android.graphics.drawable.Drawable

data class AppInfo(
    val appName: String,
    val packageName: String,
    val appIcon: Drawable,
    val uid: Int,
    var isWifiEnabled: Boolean,
    var isMobileEnabled: Boolean
)