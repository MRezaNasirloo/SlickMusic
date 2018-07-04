package com.mrezanasirloo.domain.implementation.usecase

import com.mrezanasirloo.domain.implementation.MediaPlaybackController
import com.mrezanasirloo.domain.usecase.UseCaseRequestPlaybackState
import io.reactivex.Completable
import javax.inject.Inject

class UseCaseRequestPlaybackStateImpl @Inject constructor(private val mediaPlaybackController: MediaPlaybackController) : UseCaseRequestPlaybackState() {
    override fun execute(parameter: Unit): Completable {
        return mediaPlaybackController.requestPlaybackState()
    }
}