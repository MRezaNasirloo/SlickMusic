package com.mrezanasirloo.slickmusic.presentation.ui.play.model

import com.mrezanasirloo.domain.model.AlbumDomain
import com.mrezanasirloo.domain.model.SongDomain


/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-10
 */
data class Album(private val songDomains: List<SongDomain> = emptyList()) {
    constructor(ad: AlbumDomain) : this(
            ad.songDomains
    )

    fun getId(): Int {
        return safeGetFirstSong().albumId
    }

    fun getTitle(): String {
        return safeGetFirstSong().albumName
    }

    fun getArtistId(): Int {
        return safeGetFirstSong().artistId
    }

    fun getArtistName(): String {
        return safeGetFirstSong().artistName
    }

    fun getYear(): Int {
        return safeGetFirstSong().year
    }

    fun getDateModified(): Long {
        return safeGetFirstSong().dateModified
    }

    fun getSongCount(): Int {
        return songDomains.size
    }

    fun safeGetFirstSong(): Song {
        val song: SongDomain = if (songDomains.isEmpty()) SongDomain.EMPTY_SONG else songDomains[0]
        return Song(song)

    }
}