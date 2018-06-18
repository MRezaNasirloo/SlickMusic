package com.mrezanasirloo.slickmusic.presentation.ui.play

import com.xwray.groupie.kotlinandroidextensions.Item

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-10
 */
interface ViewPlay {
    fun update(list: List<Item>)
    fun showError(error: Throwable)
}