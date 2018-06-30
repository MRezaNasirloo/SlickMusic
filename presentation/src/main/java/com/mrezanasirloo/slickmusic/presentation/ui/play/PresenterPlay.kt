package com.mrezanasirloo.slickmusic.presentation.ui.play

import com.mrezanasirloo.domain.implementation.usecase.UseCaseGetAlbumImpl
import com.mrezanasirloo.slick.uni.PartialViewState
import com.mrezanasirloo.slick.uni.SlickPresenterUni
import com.mrezanasirloo.slickmusic.presentation.ui.song.PartialStateEmptyList
import com.mrezanasirloo.slickmusic.presentation.ui.song.PartialStateError
import com.mrezanasirloo.slickmusic.presentation.ui.song.PartialStateList
import com.mrezanasirloo.slickmusic.presentation.ui.song.StateSong
import com.mrezanasirloo.slickmusic.presentation.ui.song.ViewSong
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
        private val getAllSongs: UseCaseGetAlbumImpl,
        @Named("main") main: Scheduler?,
        @Named("io") io: Scheduler?
) : SlickPresenterUni<ViewSong, StateSong>(main, io) {
    override fun start(view: ViewSong) {

//        @Suppress("RedundantSamConstructor")
        /*val list: Observable<PartialViewState<StateSong>> = getAllSongs.execute(view::albumId).subscribeOn(io)
                .map { Observable.fromIterable(it).map { Song(it) }.map { ItemSongSmall(it) }.toList().blockingGet() }
                .map(Function<List<Item>, PartialViewState<StateSong>> { PartialStateList(it) })
                .startWith(PartialStateEmptyList())
                .onErrorReturn { PartialStateError(it) }

        subscribe(StateSong(), list)*/
    }

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

data class StatePlay(
        val list: List<Item> = emptyList(),
        val error: Throwable? = null
)

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