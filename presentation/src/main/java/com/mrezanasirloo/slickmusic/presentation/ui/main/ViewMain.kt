package com.mrezanasirloo.slickmusic.presentation.ui.main

import io.reactivex.Observable

interface ViewMain {
    fun showPages()
    fun showError(error: Throwable)
    fun showRationalSettingPage()
    fun showRational()

    fun commandPermission() : Observable<Any>

}
