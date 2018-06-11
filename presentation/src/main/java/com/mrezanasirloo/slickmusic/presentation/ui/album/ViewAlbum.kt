package com.mrezanasirloo.slickmusic.presentation.ui.album

import com.xwray.groupie.kotlinandroidextensions.Item
import io.reactivex.Observable

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-10
 */
interface ViewAlbum {
    fun update(list: List<Item>)
    fun showError(error: Throwable)
}