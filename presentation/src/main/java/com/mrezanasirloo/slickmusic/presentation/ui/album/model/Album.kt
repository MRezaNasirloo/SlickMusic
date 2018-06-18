package com.mrezanasirloo.slickmusic.presentation.ui.song.model

import com.mrezanasirloo.domain.model.AlbumDomain
import com.mrezanasirloo.domain.model.SongDomain


/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-10
 */
data class Album(private val songs: List<Song> = emptyList()) {

    companion object {
        fun newInstance(ad: AlbumDomain): Album {
            val songDomains = ad.songDomains
            val mutableList: MutableList<Song> = ArrayList(songDomains.size)
            for (songDomain in songDomains) {
                mutableList.add(Song(songDomain))
            }
            return Album(mutableList.toList())
        }

        fun newInstance(songDomains: List<SongDomain>): Album {
            val mutableList: MutableList<Song> = ArrayList(songDomains.size)
            for (songDomain in songDomains) {
                mutableList.add(Song(songDomain))
            }
            return Album(mutableList.toList())
        }

    }

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
        return songs.size
    }

    fun safeGetFirstSong(): Song {
        return if (songs.isEmpty()) Song(SongDomain.EMPTY_SONG) else songs[0]
    }
}