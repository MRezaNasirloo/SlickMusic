package com.mrezanasirloo.domain.implementation.repository

import android.content.Context
import com.mrezanasirloo.data.loader.SongLoader
import com.mrezanasirloo.domain.model.SongDomain
import com.mrezanasirloo.favorite.DaoFavorite
import com.mrezanasirloo.favorite.ModelFavorite
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-07-08
 */
//Fixme use domain models
class RepoFavorite @Inject constructor(
        private val daoFavorite: DaoFavorite,
        private val context: Context
) {
    fun all(): Observable<List<SongDomain>> {
        return daoFavorite.all().toObservable()
                .flatMap { Observable.fromIterable(it) }
                .map { SongLoader.getSong(context, it.songId).copy(dbId = it.id!!) }
                .toList().toObservable()
    }

    fun insertAll(vararg songs: SongDomain): Completable {
        return Completable.create {
            songs.forEach {
                daoFavorite.insertAll(ModelFavorite(songId = it.id))
            }
            it.onComplete()
        }

    }

    fun delete(songDomain: SongDomain): Completable {
        return Completable.create {
            daoFavorite.delete(ModelFavorite(songDomain.dbId, songDomain.id))
            it.onComplete()
        }

    }
}