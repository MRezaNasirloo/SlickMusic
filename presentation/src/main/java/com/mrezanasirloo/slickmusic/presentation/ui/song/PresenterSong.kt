package com.mrezanasirloo.slickmusic.presentation.ui.song

import android.util.Log
import com.mrezanasirloo.domain.implementation.model.Song
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
        @Named("main") main: Scheduler,
        @Named("io") io: Scheduler
) : SlickPresenterUni<ViewSong, StateSong>(main, io) {
    override fun start(viewSong: ViewSong) {

        @Suppress("RedundantSamConstructor")
        val list: Observable<PartialViewState<StateSong>> = getAllSongs.execute(Unit).subscribeOn(io)
                .map { Observable.fromIterable(it).map { Song(it) }.map { ItemSongSmall(it) }.toList().blockingGet() }
                .map(Function<List<Item>, PartialViewState<StateSong>> { PartialStateList(it) })
                .startWith(PartialStateEmptyList())
                .onErrorReturn { PartialStateError(it) }

        @Suppress("RedundantSamConstructor")
        val play: Observable<PartialViewState<StateSong>> = command { view -> view.playSongs() }
                .flatMap { playSongs.execute(listOf(it.toSongDomain())).toObservable<Any>() }
                .map(Function<Any, PartialViewState<StateSong>> { NoOp() })
                .onErrorReturn { PartialStateError(it) }

        val add: Observable<PartialViewState<StateSong>> = command { view -> view.addSongToQueue() }
                .map { it.map { it.toSongDomain() } }
                .flatMap { addSongToQueue.execute(it).toObservable<Any>() }
                .map { NoOp() }


        scan(StateSong(), merge(list, play, add))
                .doOnComplete { Log.d(TAG, "onComplete() called") }
                .subscribe(this)
    }

    private val TAG: String = this::class.java.simpleName


    override fun render(state: StateSong, view: ViewSong) {
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

data class StateSong(
        val list: List<Item> = emptyList(),
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