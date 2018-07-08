package com.mrezanasirloo.slickmusic.presentation.ui.song

import android.util.Log
import com.mrezanasirloo.domain.implementation.model.Song
import com.mrezanasirloo.domain.implementation.repository.RepoFavorite
import com.mrezanasirloo.domain.implementation.usecase.UseCaseAddSongToQueueImpl
import com.mrezanasirloo.domain.implementation.usecase.UseCaseGetAllSongImpl
import com.mrezanasirloo.domain.implementation.usecase.UseCasePlaySongsImpl
import com.mrezanasirloo.slick.uni.PartialViewState
import com.mrezanasirloo.slick.uni.SlickPresenterUni
import com.mrezanasirloo.slickmusic.presentation.ui.favorite.ViewFavorite
import com.mrezanasirloo.slickmusic.presentation.ui.song.item.ItemSongSmall
import com.xwray.groupie.kotlinandroidextensions.Item
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.functions.Function
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-10
 */
class PresenterFavorite @Inject constructor(
        private val getAllSongs: UseCaseGetAllSongImpl,
        private val playSongs: UseCasePlaySongsImpl,
        private val addSongToQueue: UseCaseAddSongToQueueImpl,
        private val repoFavorite: RepoFavorite,
        @Named("main") main: Scheduler,
        @Named("io") io: Scheduler
) : SlickPresenterUni<ViewFavorite, StateFavorite>(main, io) {
    override fun start(viewSong: ViewFavorite) {

        @Suppress("RedundantSamConstructor")
        val list: Observable<PartialViewState<StateFavorite>> = repoFavorite.all().subscribeOn(io)
                .map { Observable.fromIterable(it).map { Song(it) }.map { ItemSongSmall(it) }.toList().blockingGet() }
                .map(Function<List<Item>, PartialViewState<StateFavorite>> { PartialStateListFavorite(it) })
                .startWith(PartialStateEmptyListFavorite())
                .onErrorReturn { PartialStateErrorFavorite(it) }

        @Suppress("RedundantSamConstructor")
        val deleteFav: Observable<PartialViewState<StateFavorite>> = command { v -> v.removeFromFavorite() }
                .flatMap { repoFavorite.delete(it.toSongDomain()).subscribeOn(io).toObservable<Any>() }
                .delay(1, TimeUnit.SECONDS)
                .flatMap { repoFavorite.all().subscribeOn(io) }
                .map { Observable.fromIterable(it).map { Song(it) }.map { ItemSongSmall(it) }.toList().blockingGet() }
                .map(Function<List<Item>, PartialViewState<StateFavorite>> { PartialStateListFavorite(it) })
                .onErrorReturn { PartialStateErrorFavorite(it) }

        @Suppress("RedundantSamConstructor")
        val load: Observable<PartialViewState<StateFavorite>> = command { v -> v.triggerLoad() }
                .flatMap { repoFavorite.all().subscribeOn(io) }
                .map { Observable.fromIterable(it).map { Song(it) }.map { ItemSongSmall(it) }.toList().blockingGet() }
                .map(Function<List<Item>, PartialViewState<StateFavorite>> { PartialStateListFavorite(it) })
                .onErrorReturn { PartialStateErrorFavorite(it) }

        @Suppress("RedundantSamConstructor")
        val play: Observable<PartialViewState<StateFavorite>> = command { view -> view.playSongs() }
                .flatMap { playSongs.execute(listOf(it.toSongDomain())).toObservable<Any>() }
                .map(Function<Any, PartialViewState<StateFavorite>> { NoOpFavorite() })
                .onErrorReturn { PartialStateErrorFavorite(it) }


        val addToQueue: Observable<PartialViewState<StateFavorite>> = command { view -> view.addSongToQueue() }
                .map { it.map { it.toSongDomain() } }
                .flatMap { addSongToQueue.execute(it).toObservable<Any>() }
                .map { NoOpFavorite() }


        scan(StateFavorite(), merge(list, play, addToQueue, deleteFav, load))
                .doOnComplete { Log.d(TAG, "onComplete() called") }
                .subscribe(this)
    }

    private val TAG: String = this::class.java.simpleName


    var oldState: StateFavorite? = null
    override fun render(state: StateFavorite, view: ViewFavorite) {
        oldState = state
        println("list = [${state.list.size}], error = [${state.error}] view = [$view]")
        view.apply {
            view.update(state.list)
            state.error?.let {
                //TODO Handle errors here
                showError(it)
            }
        }
    }
}

data class StateFavorite(
        val list: List<Item> = emptyList(),
        val error: Throwable? = null
)

class NoOpFavorite : PartialViewState<StateFavorite> {
    override fun reduce(state: StateFavorite?): StateFavorite {
        return state!!
    }
}

class PartialStateListFavorite(private val list: List<Item>) : PartialViewState<StateFavorite> {
    override fun reduce(state: StateFavorite?): StateFavorite {
        return state!!.copy(list = list)
    }
}


class PartialStateEmptyListFavorite : PartialViewState<StateFavorite> {
    override fun reduce(state: StateFavorite?): StateFavorite {
        return state!!.copy(list = emptyList())
    }
}

class PartialStateErrorFavorite(private val error: Throwable) : PartialViewState<StateFavorite> {
    override fun reduce(state: StateFavorite?): StateFavorite {
        return state!!.copy(error = error)
    }
}