package com.mrezanasirloo.slickmusic.presentation.ui.play

import com.mrezanasirloo.domain.model.PlaybackStateDomain
import com.xwray.groupie.kotlinandroidextensions.Item
import io.reactivex.Observable

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-10
 */
interface ViewPlay {
    fun updateQueue(list: List<Item>)
    fun updateState(playbackState: PlaybackStateDomain)

    fun play(): Observable<Any>
    fun pause(): Observable<Any>
    fun next(): Observable<Any>
    fun previous(): Observable<Any>
    fun seekTo(): Observable<Int>

    fun showError(error: Throwable)
}