package com.mrezanasirloo.domain.usecase

import com.mrezanasirloo.domain.model.SongDomain
import io.reactivex.Observable

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-09
 */
abstract class UseCaseQueueUpdates : UseCase<Unit, Observable<Collection<SongDomain>>>()
