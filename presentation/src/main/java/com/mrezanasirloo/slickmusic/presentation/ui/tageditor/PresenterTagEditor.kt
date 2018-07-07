package com.mrezanasirloo.slickmusic.presentation.ui.tageditor

import com.mrezanasirloo.domain.implementation.repository.RepositoryID3Tag
import com.mrezanasirloo.domain.model.SongTagDomain
import com.mrezanasirloo.slick.uni.PartialViewState
import com.mrezanasirloo.slick.uni.SlickPresenterUni
import io.reactivex.Observable
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-07-07
 */
class PresenterTagEditor @Inject constructor(
        private val repositoryID3Tag: RepositoryID3Tag,
        @Named("main") main: Scheduler,
        @Named("io") io: Scheduler
) : SlickPresenterUni<ViewTagEditor, StateTagEditor>(
        main, io
) {
    override fun start(view: ViewTagEditor) {
        val song = view.song()

        val data: Observable<PartialViewState<StateTagEditor>> = repositoryID3Tag.readId3Tag(song.toSongDomain())
                .map { PartialSongData(it) }

        scan(StateTagEditor(), data).subscribe(this)
    }

    override fun render(state: StateTagEditor, view: ViewTagEditor) {

    }
}

data class StateTagEditor(val songTagDomain: SongTagDomain? = null)

class PartialSongData(private val songTagDomain: SongTagDomain) : PartialViewState<StateTagEditor> {
    override fun reduce(state: StateTagEditor?): StateTagEditor {
        return state!!.copy(songTagDomain = songTagDomain)
    }
}
