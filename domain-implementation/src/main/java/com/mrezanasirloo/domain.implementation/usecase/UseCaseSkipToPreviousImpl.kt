package com.mrezanasirloo.domain.implementation.usecase

import com.mrezanasirloo.domain.implementation.MediaPlaybackController
import com.mrezanasirloo.domain.usecase.UseCaseSkipToPrevious
import io.reactivex.Completable
import javax.inject.Inject

class UseCaseSkipToPreviousImpl @Inject constructor(private val mediaPlaybackController: MediaPlaybackController) : UseCaseSkipToPrevious() {
    override fun execute(parameter: Unit): Completable {
        return mediaPlaybackController.previous()
    }
}