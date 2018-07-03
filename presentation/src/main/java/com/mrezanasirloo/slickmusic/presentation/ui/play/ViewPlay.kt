package com.mrezanasirloo.slickmusic.presentation.ui.play

import com.mrezanasirloo.slickmusic.presentation.ui.song.model.Song

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-10
 */
interface ViewPlay {
    /*fun update(list: List<Item>)
    fun showError(error: Throwable)*/
    fun playSong(vararg song: Song)
}