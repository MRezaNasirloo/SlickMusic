package com.mrezanasirloo.slickmusic.presentation.ui.tageditor

import com.mrezanasirloo.domain.implementation.model.Song
import com.mrezanasirloo.domain.model.SongTagDomain
import io.reactivex.Observable

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-07-07
 */
interface ViewTagEditor {
    fun song(): Song
    fun showSong(songTag: SongTagDomain)
    fun save(): Observable<SongTagDomain>
}