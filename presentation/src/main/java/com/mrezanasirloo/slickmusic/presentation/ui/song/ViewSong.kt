package com.mrezanasirloo.slickmusic.presentation.ui.song

import com.xwray.groupie.kotlinandroidextensions.Item

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-10
 */
interface ViewSong {
    fun update(list: List<Item>)
    fun showError(error: Throwable)
}