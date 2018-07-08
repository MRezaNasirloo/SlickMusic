package com.mrezanasirloo.domain.model

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-09
 */
data class SongDomain(
        val dbId: Int = -1,
        val id: Int = -1,
        val title: String = "",
        val trackNumber: Int = -1,
        val year: Int = -1,
        val duration: Long = -1,
        val data: String = "",
        val dateModified: Long = -1,
        val albumId: Int = -1,
        val albumName: String = "",
        val artistId: Int = -1,
        val artistName: String = ""
) {
    companion object {
        val EMPTY_SONG = SongDomain()
    }

}