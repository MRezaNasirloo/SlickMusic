package com.mrezanasirloo.slickmusic.presentation.ui.play

import com.mrezanasirloo.domain.model.PlaybackStateDomain
import io.reactivex.Observable

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-10
 */
interface ViewPlay {
    //    fun update(list: List<Item>)
    fun showError(error: Throwable)

    fun updateState(playbackState: PlaybackStateDomain)
    fun play(): Observable<Any>
    fun pause(): Observable<Any>
}