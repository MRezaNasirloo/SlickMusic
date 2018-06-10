package com.mrezanasirloo.domain.implementation.usecase

import com.mrezanasirloo.domain.implementation.RepositorySongImpl
import com.mrezanasirloo.domain.model.SongDomain
import com.mrezanasirloo.domain.usecase.UseCaseGetAllSongs
import io.reactivex.Observable
import javax.inject.Inject

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-10
 */
class UseCaseGetAllSongImpl @Inject constructor(private val repo: RepositorySongImpl) : UseCaseGetAllSongs() {

    override fun execute(): Observable<List<SongDomain>> {
        return Observable.create({ emitter ->
            emitter.onNext(repo.allSongs())
            emitter.onComplete()
        })
    }
}