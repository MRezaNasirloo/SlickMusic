package com.mrezanasirloo.slickmusic.presentation.ui.song

import android.util.Log
import com.mrezanasirloo.domain.implementation.model.Song
import com.mrezanasirloo.domain.implementation.repository.RepoFavorite
import com.mrezanasirloo.domain.implementation.usecase.UseCaseAddSongToQueueImpl
import com.mrezanasirloo.domain.implementation.usecase.UseCaseGetAllSongImpl
import com.mrezanasirloo.domain.implementation.usecase.UseCasePlaySongsImpl
import com.mrezanasirloo.slick.uni.PartialViewState
import com.mrezanasirloo.slick.uni.SlickPresenterUni
import com.mrezanasirloo.slickmusic.presentation.ui.song.item.ItemSongSmall
import com.xwray.groupie.kotlinandroidextensions.Item
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.functions.Function
import javax.inject.Inject
import javax.inject.Named

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-10
 */
class PresenterSong @Inject constructor(
        private val getAllSongs: UseCaseGetAllSongImpl,
        private val playSongs: UseCasePlaySongsImpl,
        private val addSongToQueue: UseCaseAddSongToQueueImpl,
        private val repoFavorite: RepoFavorite,
        @Named("main") main: Scheduler,
        @Named("io") io: Scheduler
) : SlickPresenterUni<ViewSong, StateSong>(main, io) {
    override fun start(viewSong: ViewSong) {

        @Suppress("RedundantSamConstructor")
        val list: Observable<PartialViewState<StateSong>> = command { v ->
            viewSong.trigger().startWith(1).flatMap {
                getAllSongs.execute(Unit).subscribeOn(io)
                        .map { Observable.fromIterable(it).map { Song(it) }.map { ItemSongSmall(it) }.toList().blockingGet() }
                        .map(Function<List<Item>, PartialViewState<StateSong>> { PartialStateList(it) })
                        .startWith(PartialStateEmptyList())
                        .onErrorReturn { PartialStateError(it) }
            }.onErrorReturn { PartialStateError(it) }
        }

        @Suppress("RedundantSamConstructor")
        val play: Observable<PartialViewState<StateSong>> = command { view -> view.playSongs() }
                .flatMap { playSongs.execute(listOf(it.toSongDomain())).toObservable<Any>() }
                .map(Function<Any, PartialViewState<StateSong>> { NoOp() })
                .onErrorReturn { PartialStateError(it) }

        @Suppress("RedundantSamConstructor")
        val addFavorite: Observable<PartialViewState<StateSong>> = command { view ->
            view.addToFavorite().flatMap {
                repoFavorite.insertAll(it.toSongDomain()).toObservable<Any>().subscribeOn(io)
                        .map(Function<Any, PartialViewState<StateSong>> { NoOp() })
                        .onErrorReturn { PartialStateError(it) }
            }.onErrorReturn { PartialStateError(it) }
        }.onErrorReturn { PartialStateError(it) }

        @Suppress("RedundantSamConstructor")
        val search: Observable<PartialViewState<StateSong>> = command { view -> view.search() }
                .flatMap { term ->
                    Observable.fromIterable(oldState?.list)
                            .filter {
                                it as ItemSongSmall
                                val song = it.song
                                song.artistName.contains(term, true) || song.albumName.contains(term, true) || song.title.contains(term, true)
                            }.toList().toObservable()
                }
                .map(Function<List<Item>, PartialViewState<StateSong>> { PartialStateSearchResult(it) })
                .onErrorReturn { PartialStateError(it) }

        @Suppress("RedundantSamConstructor")
        val searchClose: Observable<PartialViewState<StateSong>> = command { view -> view.searchClose() }
                .map { emptyList<Item>() }
                .map { PartialStateSearchResult(emptyList()) }

        val add: Observable<PartialViewState<StateSong>> = command { view -> view.addSongToQueue() }
                .map { it.map { it.toSongDomain() } }
                .flatMap { addSongToQueue.execute(it).toObservable<Any>() }
                .map { NoOp() }


        scan(StateSong(), merge(list, play, add, search, searchClose, addFavorite))
                .doOnComplete { Log.d(TAG, "onComplete() called") }
                .subscribe(this)
    }

    private val TAG: String = this::class.java.simpleName


    var oldState: StateSong? = null
    override fun render(state: StateSong, view: ViewSong) {
        oldState = state
        println("list = [${state.list.size}], error = [${state.error}] view = [$view]")
        view.apply {
            if (!state.search.isEmpty()) view.update(state.search) else view.update(state.list)
            state.error?.let {
                //TODO Handle errors here
                showError(it)
            }
        }
    }
}

data class StateSong(
        val list: List<Item> = emptyList(),
        val search: List<Item> = emptyList(),
        val error: Throwable? = null
)

class NoOp : PartialViewState<StateSong> {
    override fun reduce(state: StateSong?): StateSong {
        return state!!
    }
}

class PartialStateList(private val list: List<Item>) : PartialViewState<StateSong> {
    override fun reduce(state: StateSong?): StateSong {
        return state!!.copy(list = list)
    }
}

class PartialStateSearchResult(private val list: List<Item>) : PartialViewState<StateSong> {
    override fun reduce(state: StateSong?): StateSong {
        return state!!.copy(search = list)
    }
}

class PartialStateEmptyList : PartialViewState<StateSong> {
    override fun reduce(state: StateSong?): StateSong {
        return state!!.copy(list = emptyList())
    }
}

class PartialStateError(private val error: Throwable) : PartialViewState<StateSong> {
    override fun reduce(state: StateSong?): StateSong {
        return state!!.copy(error = error)
    }
}