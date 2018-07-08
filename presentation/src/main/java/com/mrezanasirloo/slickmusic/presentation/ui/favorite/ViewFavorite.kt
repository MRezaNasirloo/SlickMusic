package com.mrezanasirloo.slickmusic.presentation.ui.favorite

import com.mrezanasirloo.domain.implementation.model.Song
import com.xwray.groupie.kotlinandroidextensions.Item
import io.reactivex.Observable

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-10
 */
interface ViewFavorite {
    fun update(list: List<Item>)
    fun showError(error: Throwable)
    fun playSongs(): Observable<Song>
    fun triggerLoad(): Observable<Any>
    fun removeFromFavorite(): Observable<Song>
    fun addSongToQueue(): Observable<Collection<Song>>
}