package com.mrezanasirloo.domain.model

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-10
 */
data class AlbumDomain(val songDomains: List<SongDomain> = emptyList()) {
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

    private fun safeGetFirstSong(): SongDomain {
        return if (songDomains.isEmpty()) SongDomain.EMPTY_SONG else songDomains[0]
    }
}