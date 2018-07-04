package com.mrezanasirloo.domain.implementation.usecase

import com.mrezanasirloo.domain.implementation.MediaPlaybackController
import com.mrezanasirloo.domain.model.SongDomain
import com.mrezanasirloo.domain.usecase.UseCasePlaySongs
import io.reactivex.Completable
import javax.inject.Inject

class UseCasePlaySongsImpl @Inject constructor(
        private val mediaPlaybackController: MediaPlaybackController
) : UseCasePlaySongs() {

    override fun execute(parameter: Iterable<SongDomain>): Completable {
        return mediaPlaybackController.playSongs(parameter)
    }
}