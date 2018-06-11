package com.mrezanasirloo.slickmusic.presentation.ui.song

import com.mrezanasirloo.domain.implementation.usecase.UseCaseGetAllSongImpl
import com.mrezanasirloo.slick.uni.PartialViewState
import com.mrezanasirloo.slick.uni.SlickPresenterUni
import com.mrezanasirloo.slickmusic.presentation.ui.song.item.ItemSongSmall
import com.mrezanasirloo.slickmusic.presentation.ui.song.model.Song
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
class PresenterPlay @Inject constructor(
        private val getAllSongs: UseCaseGetAllSongImpl,
        @Named("main") main: Scheduler?,
        @Named("io") io: Scheduler?
) : SlickPresenterUni<ViewSong, StatePlay>(main, io) {
    override fun start(view: ViewSong) {

        @Suppress("RedundantSamConstructor")
        val list: Observable<PartialViewState<StatePlay>> = getAllSongs.execute().subscribeOn(io)
                .map { Observable.fromIterable(it).map { Song(it) }.map { ItemSongSmall(it) }.toList().blockingGet() }
                .map(Function<List<Item>, PartialViewState<StatePlay>> { PartialStateList(it) })
                .startWith(PartialStateEmptyList())
                .onErrorReturn { PartialStateError(it) }

        subscribe(StatePlay(), list)
    }

    override fun render(state: StatePlay, view: ViewSong) {
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

data class StatePlay(
        val list: List<Item> = emptyList(),
        val error: Throwable? = null
)

class PartialStateList(private val list: List<Item>) : PartialViewState<StatePlay> {
    override fun reduce(state: StatePlay?): StatePlay {
        return state!!.copy(list = list)
    }
}

class PartialStateEmptyList : PartialViewState<StatePlay> {
    override fun reduce(state: StatePlay?): StatePlay {
        return state!!.copy(list = emptyList())
    }
}

class PartialStateError(private val error: Throwable) : PartialViewState<StatePlay> {
    override fun reduce(state: StatePlay?): StatePlay {
        return state!!.copy(error = error)
    }
}