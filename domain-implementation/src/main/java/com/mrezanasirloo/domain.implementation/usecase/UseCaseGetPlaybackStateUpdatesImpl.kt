package com.mrezanasirloo.domain.implementation.usecase

import com.mrezanasirloo.domain.implementation.MediaPlaybackController
import com.mrezanasirloo.domain.model.PlaybackStateDomain
import com.mrezanasirloo.domain.usecase.UseCaseGetPlaybackStateUpdates
import io.reactivex.Observable
import javax.inject.Inject

class UseCaseGetPlaybackStateUpdatesImpl @Inject constructor(
        private val mediaPlaybackController: MediaPlaybackController
) : UseCaseGetPlaybackStateUpdates() {

    override fun execute(parameter: Unit): Observable<PlaybackStateDomain> {
        return mediaPlaybackController.playbackStateUpdate()
    }
}