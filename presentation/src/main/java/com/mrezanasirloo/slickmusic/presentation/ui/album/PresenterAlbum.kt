package com.mrezanasirloo.slickmusic.presentation.ui.album

import com.mrezanasirloo.domain.implementation.usecase.UseCaseGetAllAlbumImpl
import com.mrezanasirloo.domain.usecase.UseCaseGetAllAlbums
import com.mrezanasirloo.slick.uni.PartialViewState
import com.mrezanasirloo.slick.uni.SlickPresenterUni
import com.mrezanasirloo.slickmusic.presentation.ui.album.item.ItemAlbumBig
import com.mrezanasirloo.slickmusic.presentation.ui.play.item.ItemSongSmall
import com.mrezanasirloo.slickmusic.presentation.ui.play.model.Album
import com.mrezanasirloo.slickmusic.presentation.ui.play.model.Song
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
class PresenterAlbum @Inject constructor(
        private val getAllAlbums: UseCaseGetAllAlbumImpl,
        @Named("main") main: Scheduler?,
        @Named("io") io: Scheduler?
) : SlickPresenterUni<ViewAlbum, StateAlbum>(main, io) {
    override fun start(view: ViewAlbum) {

        @Suppress("RedundantSamConstructor")
        val list: Observable<PartialViewState<StateAlbum>> = getAllAlbums.execute().subscribeOn(io)
                .map { Observable.fromIterable(it).map { Album(it) }.map { ItemAlbumBig(it) }.toList().blockingGet() }
                .map(Function<List<Item>, PartialViewState<StateAlbum>> { PartialStateListAlbum(it) })
                .startWith(PartialStateEmptyListAlbum())
                .onErrorReturn { PartialStateErrorAlbum(it) }

        subscribe(StateAlbum(), list)
    }

    override fun render(state: StateAlbum, view: ViewAlbum) {
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

data class StateAlbum(
        val list: List<Item> = emptyList(),
        val error: Throwable? = null
)

class PartialStateListAlbum(private val list: List<Item>) : PartialViewState<StateAlbum> {
    override fun reduce(state: StateAlbum?): StateAlbum {
        return state!!.copy(list = list)
    }
}

class PartialStateEmptyListAlbum : PartialViewState<StateAlbum> {
    override fun reduce(state: StateAlbum?): StateAlbum {
        return state!!.copy(list = emptyList())
    }
}

class PartialStateErrorAlbum(private val error: Throwable) : PartialViewState<StateAlbum> {
    override fun reduce(state: StateAlbum?): StateAlbum {
        return state!!.copy(error = error)
    }
}

