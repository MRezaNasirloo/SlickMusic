package com.mrezanasirloo.domain.interactor

import com.mrezanasirloo.domain.model.SongDomain
import io.reactivex.Completable

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-07-04
 */
interface InteractorPlayback {
    fun playSong(song: SongDomain): Completable
    fun play(): Completable
    fun pause(): Completable
    fun next(): Completable
    fun previous(): Completable
    fun seekTo(position: Int): Completable
    fun addQueueSong(songs: Iterable<SongDomain>): Completable
    fun skipToQueueSong(id: Long): Completable
    fun playSongs(songs: Iterable<SongDomain>): Completable
}