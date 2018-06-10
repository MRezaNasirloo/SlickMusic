package com.mrezanasirloo.domain.model

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-10
 */
const val UNKNOWN_ARTIST_DISPLAY_NAME = "Unknown ArtistDomain"

data class ArtistDomain(val albumDomains: List<AlbumDomain> = emptyList()) {
    fun getId(): Int {
        return safeGetFirstAlbum().getArtistId()
    }

    fun getName(): String {
        val name = safeGetFirstAlbum().getArtistName()
        return if (isArtistNameUnknown(name)) {
            UNKNOWN_ARTIST_DISPLAY_NAME
        } else name
    }

    fun getSongCount(): Int {
        var songCount = 0
        for (album in albumDomains) {
            songCount += album.getSongCount()
        }
        return songCount
    }

    fun getAlbumCount(): Int {
        return albumDomains.size
    }

    fun getSongs(): List<SongDomain> {
        val songDomains: MutableList<SongDomain> = mutableListOf()
        for ((songs1) in albumDomains) {
            songDomains.addAll(songs1)
        }
        return songDomains
    }

    private fun safeGetFirstAlbum(): AlbumDomain {
        return if (albumDomains.isEmpty()) AlbumDomain() else albumDomains[0]
    }

    private fun isArtistNameUnknown(artistName: String): Boolean {
        var varArtistName = artistName
        if (varArtistName.isEmpty()) return false
        if (varArtistName == UNKNOWN_ARTIST_DISPLAY_NAME) return true
        varArtistName = varArtistName.trim().toLowerCase()
        return varArtistName == "unknown" || varArtistName == "<unknown>"
    }
}