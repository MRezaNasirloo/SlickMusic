package com.mrezanasirloo.domain.implementation.usecase

import com.mrezanasirloo.domain.implementation.MediaPlaybackController
import com.mrezanasirloo.domain.usecase.UseCaseSeekTo
import io.reactivex.Completable
import javax.inject.Inject

class UseCaseSeekToImpl @Inject constructor(private val mediaPlaybackController: MediaPlaybackController) : UseCaseSeekTo() {
    override fun execute(parameter: Int): Completable {
        return mediaPlaybackController.seekTo(parameter)
    }
}