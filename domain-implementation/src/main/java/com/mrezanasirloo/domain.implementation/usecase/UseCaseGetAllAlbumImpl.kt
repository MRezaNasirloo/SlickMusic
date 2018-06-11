package com.mrezanasirloo.domain.implementation.usecase

import com.mrezanasirloo.domain.implementation.repository.RepositoryAlbumImpl
import com.mrezanasirloo.domain.implementation.repository.RepositorySongImpl
import com.mrezanasirloo.domain.model.AlbumDomain
import com.mrezanasirloo.domain.model.SongDomain
import com.mrezanasirloo.domain.usecase.UseCaseGetAllAlbums
import com.mrezanasirloo.domain.usecase.UseCaseGetAllSongs
import io.reactivex.Observable
import javax.inject.Inject

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-10
 */
class UseCaseGetAllAlbumImpl @Inject constructor(private val repo: RepositoryAlbumImpl) : UseCaseGetAllAlbums() {

    override fun execute(): Observable<List<AlbumDomain>> {
        return Observable.create({ emitter ->
            emitter.onNext(repo.allAlbums())
            emitter.onComplete()
        })
    }
}