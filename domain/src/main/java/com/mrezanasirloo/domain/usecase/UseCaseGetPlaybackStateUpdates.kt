package com.mrezanasirloo.domain.usecase

import com.mrezanasirloo.domain.model.PlaybackStateDomain
import io.reactivex.Observable

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-09
 */
abstract class UseCaseGetPlaybackStateUpdates : UseCase<Unit, Observable<PlaybackStateDomain>>()
