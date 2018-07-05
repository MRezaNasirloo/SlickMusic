package com.mrezanasirloo.domain.usecase

import com.mrezanasirloo.domain.model.SongDomain
import io.reactivex.Completable

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-07-06
 */
abstract class UseCaseAddSongToQueue : UseCase<Collection<SongDomain>, Completable>()
