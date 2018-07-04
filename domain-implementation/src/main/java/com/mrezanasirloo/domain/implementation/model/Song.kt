package com.mrezanasirloo.domain.implementation.model

import android.os.Parcelable
import com.mrezanasirloo.domain.model.SongDomain
import kotlinx.android.parcel.Parcelize

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-10
 */
@Parcelize
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
) : Parcelable {
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