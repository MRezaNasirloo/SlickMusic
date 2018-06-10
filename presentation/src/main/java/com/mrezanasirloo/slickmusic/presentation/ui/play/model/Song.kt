package com.mrezanasirloo.slickmusic.presentation.ui.play.model

import com.mrezanasirloo.domain.model.SongDomain

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-10
 */
data class Song(
        val id: Int,
        val title: String,
        val trackNumber: Int,
        val year: Int,
        val duration: Long,
        val data: String,
        val dateModified: Long,
        val albumId: Int,
        val albumName: String,
        val artistId: Int,
        val artistName: String
) {
    constructor(sd: SongDomain) : this(
            sd.id,
            sd.title,
            sd.trackNumber,
            sd.year,
            sd.duration,
            sd.data,
            sd.dateModified,
            sd.albumId,
            sd.albumName,
            sd.artistId,
            sd.artistName
    )
}