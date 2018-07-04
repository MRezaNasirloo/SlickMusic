package com.mrezanasirloo.domain.implementation.usecase

import com.mrezanasirloo.domain.implementation.MediaPlaybackController
import com.mrezanasirloo.domain.usecase.UseCasePlay
import io.reactivex.Completable
import javax.inject.Inject

class UseCasePlayImpl @Inject constructor(private val mediaPlaybackController: MediaPlaybackController) : UseCasePlay() {
    override fun execute(parameter: Unit): Completable {
        return mediaPlaybackController.play()
    }
}