package com.mrezanasirloo.domain.interactor

import com.mrezanasirloo.domain.model.PlaybackStateDomain
import io.reactivex.Observable

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-07-04
 */
interface InteractorPlaybackState {
    fun playbackStateUpdate() : Observable<PlaybackStateDomain>
}