package com.mrezanasirloo.slickmusic.presentation.ui.play

import com.mrezanasirloo.domain.implementation.usecase.*
import com.mrezanasirloo.domain.model.PlaybackStateDomain
import com.mrezanasirloo.slick.uni.PartialViewState
import com.mrezanasirloo.slick.uni.SlickPresenterUni
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
        private val getPlaybackUpdates: UseCaseGetPlaybackStateUpdatesImpl,
        private val play: UseCasePlayImpl,
        private val pause: UseCasePauseImpl,
        private val seekTo: UseCaseSeekToImpl,
        private val skipToNext: UseCaseSkipToNextImpl,
        private val skipToPrevious: UseCaseSkipToPreviousImpl,
        @Named("main") main: Scheduler,
        @Named("io") io: Scheduler
) : SlickPresenterUni<ViewPlay, StatePlay>(main, io) {
    override fun start(viewPlay: ViewPlay) {

        @Suppress("RedundantSamConstructor")
        val playbackState: Observable<PartialViewState<StatePlay>> = getPlaybackUpdates.execute(Unit)
                .map(Function<PlaybackStateDomain, PartialViewState<StatePlay>> { PartialPlaybackState(it) })
                .startWith(PartialStateEmptyList())
                .onErrorReturn { PartialStateError(it) }

        val play: Observable<PartialViewState<StatePlay>> = command { view -> view.play() }
                .flatMap { play.execute(Unit).toObservable<Unit>() }
                .map { NoOp() }

        val pause: Observable<PartialViewState<StatePlay>> = command { view -> view.pause() }
                .flatMap { pause.execute(Unit).toObservable<Unit>() }
                .map { NoOp() }
        val seek: Observable<PartialViewState<StatePlay>> = command { view -> view.seekTo() }
                .flatMap { seekTo.execute(it).toObservable<Int>() }
                .map { NoOp() }
        val next: Observable<PartialViewState<StatePlay>> = command { view -> view.next() }
                .flatMap { skipToNext.execute(Unit).toObservable<Int>() }
                .map { NoOp() }
        val previous: Observable<PartialViewState<StatePlay>> = command { view -> view.previous() }
                .flatMap { skipToPrevious.execute(Unit).toObservable<Int>() }
                .map { NoOp() }

        subscribe(StatePlay(), merge(playbackState, play, pause, seek, next, previous))
    }

    override fun render(state: StatePlay, view: ViewPlay) {
        view.apply {
            updateState(state.playbackState)
            state.error?.let {
                //TODO Handle errors here
                showError(it)
            }
        }
    }
}

data class StatePlay(
        val list: List<Item> = emptyList(),
        val playbackState: PlaybackStateDomain = PlaybackStateDomain(actions = 0),
        val error: Throwable? = null
)

class PartialPlaybackState(private val playbackState: PlaybackStateDomain) : PartialViewState<StatePlay> {
    override fun reduce(state: StatePlay?): StatePlay {
        return state!!.copy(playbackState = playbackState)
    }
}

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

class NoOp : PartialViewState<StatePlay> {
    override fun reduce(state: StatePlay?): StatePlay {
        return state!!
    }
}


class PartialStateError(private val error: Throwable) : PartialViewState<StatePlay> {
    override fun reduce(state: StatePlay?): StatePlay {
        return state!!.copy(error = error)
    }
}
