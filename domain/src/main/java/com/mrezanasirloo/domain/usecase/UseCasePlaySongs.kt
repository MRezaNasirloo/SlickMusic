package com.mrezanasirloo.domain.usecase

import com.mrezanasirloo.domain.model.SongDomain
import io.reactivex.Completable

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-09
 */
abstract class UseCasePlaySongs : UseCase<Iterable<SongDomain>, Completable>()
