package com.mrezanasirloo.data

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-07-08
 */
class SharedPrefHelper constructor(val context: Context) {
    private val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
}