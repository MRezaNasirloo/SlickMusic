package com.mrezanasirloo.domain.implementation.usecase

import com.mrezanasirloo.domain.implementation.MediaPlaybackController
import com.mrezanasirloo.domain.usecase.UseCaseSkipToNext
import io.reactivex.Completable
import javax.inject.Inject

class UseCaseSkipToNextImpl @Inject constructor(private val mediaPlaybackController: MediaPlaybackController) : UseCaseSkipToNext() {
    override fun execute(parameter: Unit): Completable {
        return mediaPlaybackController.next()
    }
}