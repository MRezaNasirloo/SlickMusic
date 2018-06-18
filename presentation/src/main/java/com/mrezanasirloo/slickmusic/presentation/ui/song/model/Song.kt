package com.mrezanasirloo.slickmusic.presentation.ui.song.model

import android.os.Parcel
import android.os.Parcelable
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
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readLong(),
            parcel.readString(),
            parcel.readLong(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString()) {
    }

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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeInt(trackNumber)
        parcel.writeInt(year)
        parcel.writeLong(duration)
        parcel.writeString(data)
        parcel.writeLong(dateModified)
        parcel.writeInt(albumId)
        parcel.writeString(albumName)
        parcel.writeInt(artistId)
        parcel.writeString(artistName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Song> {
        override fun createFromParcel(parcel: Parcel): Song {
            return Song(parcel)
        }

        override fun newArray(size: Int): Array<Song?> {
            return arrayOfNulls(size)
        }
    }
}