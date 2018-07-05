package com.mrezanasirloo.domain.implementation.usecase

import com.mrezanasirloo.domain.implementation.MediaPlaybackController
import com.mrezanasirloo.domain.model.SongDomain
import com.mrezanasirloo.domain.usecase.UseCaseAddSongToQueue
import io.reactivex.Completable
import javax.inject.Inject

class UseCaseAddSongToQueueImpl @Inject constructor(private val mediaPlaybackController: MediaPlaybackController) : UseCaseAddSongToQueue() {
    override fun execute(parameter: Collection<SongDomain>): Completable {
        return mediaPlaybackController.addQueueSong(parameter)
    }
}