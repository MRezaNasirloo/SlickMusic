package com.mrezanasirloo.slickmusic.presentation.ui.play

import com.mrezanasirloo.domain.implementation.usecase.UseCaseGetAllSongImpl
import com.mrezanasirloo.domain.implementation.usecase.UseCasePermissionReadExternalStorageImpl
import com.mrezanasirloo.slick.uni.PartialViewState
import com.mrezanasirloo.slick.uni.SlickPresenterUni
import com.mrezanasirloo.slickmusic.presentation.ui.play.item.ItemSongSmall
import com.mrezanasirloo.slickmusic.presentation.ui.play.model.Song
import com.vanniktech.rxpermission.Permission
import com.vanniktech.rxpermission.Permission.State.*
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
        private val getReadPermission: UseCasePermissionReadExternalStorageImpl,
        @Named("main") main: Scheduler?,
        @Named("io") io: Scheduler?
) : SlickPresenterUni<ViewPlay, StatePlay>(main, io) {
    override fun start(view: ViewPlay) {
        val permissionRead = command(ViewPlay::commandPermission)
                .startWith(1)
                .flatMap { getReadPermission.execute().toObservable() }
                .doOnEach {
                    println("onEach called: ${it.value}")
                }
                .share().replay(1).autoConnect()

        @Suppress("RedundantSamConstructor")
        val permission: Observable<PartialViewState<StatePlay>> = permissionRead
                .map(Function<Permission, PartialViewState<StatePlay>> { PartialStatePermission(it.state()) })
                .onErrorReturn { PartialStateError(it) }

        @Suppress("RedundantSamConstructor")
        val list: Observable<PartialViewState<StatePlay>> = permissionRead
                .filter({ it.state() == GRANTED })
                .flatMap { getAllSongs.execute().subscribeOn(io) }
                .map { Observable.fromIterable(it).map { Song(it) }.map { ItemSongSmall(it) }.toList().blockingGet() }
                .map(Function<List<Item>, PartialViewState<StatePlay>> { PartialStateList(it) })
                .startWith(PartialStateEmptyList())
                .onErrorReturn { PartialStateError(it) }

        subscribe(StatePlay(), merge(permission, list))
    }

    override fun render(state: StatePlay, view: ViewPlay) {
        println("state = [$state], view = [$view]")
        view.apply {
            when (state.permissionState) {
                GRANTED -> update(state.list)
                DENIED -> showRational()
                DENIED_NOT_SHOWN -> showRationalSettingPage()
                REVOKED_BY_POLICY -> showError(Throwable("REVOKED_BY_POLICY"))
            }
            state.error?.let {
                //TODO Handle errors here
                showError(it)
            }
        }
    }
}

data class StatePlay(
        val list: List<Item> = emptyList(),
        val permissionState: Permission.State = DENIED,
        val error: Throwable? = null
)

class PartialStateList(private val list: List<Item>) : PartialViewState<StatePlay> {
    override fun reduce(state: StatePlay?): StatePlay {
        return state!!.copy(list = list)
    }
}

class PartialStatePermission(private val permissionState: Permission.State) : PartialViewState<StatePlay> {
    override fun reduce(state: StatePlay?): StatePlay {
        return state!!.copy(permissionState = permissionState)
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