package com.mrezanasirloo.domain.implementation.usecase

import com.mrezanasirloo.domain.implementation.MediaPlaybackController
import com.mrezanasirloo.domain.model.SongDomain
import com.mrezanasirloo.domain.usecase.UseCaseQueueUpdates
import io.reactivex.Observable
import javax.inject.Inject

class UseCaseQueueUpdatesImpl @Inject constructor(private val mediaPlaybackController: MediaPlaybackController) : UseCaseQueueUpdates() {
    override fun execute(parameter: Unit): Observable<Collection<SongDomain>> {
        return mediaPlaybackController.queueUpdates()
    }
}