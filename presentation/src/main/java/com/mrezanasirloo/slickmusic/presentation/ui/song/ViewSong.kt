package com.mrezanasirloo.slickmusic.presentation.ui.song

import com.mrezanasirloo.domain.implementation.model.Song
import com.xwray.groupie.kotlinandroidextensions.Item
import io.reactivex.Observable

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-10
 */
interface ViewSong {
    fun update(list: List<Item>)
    fun showError(error: Throwable)
    fun playSongs(): Observable<Song>
    fun search(): Observable<String>
    fun searchClose(): Observable<Any>
    fun trigger(): Observable<Any>
    fun addToFavorite(): Observable<Song>
    fun addSongToQueue(): Observable<Collection<Song>>
}