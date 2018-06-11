package com.mrezanasirloo.slickmusic.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-11
 */

fun Context.openAppSettingPage() {
    val intent = Intent()
    intent.apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        addCategory(Intent.CATEGORY_DEFAULT)
        data = Uri.parse("package:$packageName")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
    }
    startActivity(intent)
}