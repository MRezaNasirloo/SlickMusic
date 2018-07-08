package com.mrezanasirloo.domain.implementation.repository

import android.content.Context
import android.media.MediaScannerConnection
import com.mrezanasirloo.domain.model.SongDomain
import com.mrezanasirloo.domain.model.SongTagDomain
import io.reactivex.Observable
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import javax.inject.Inject

class RepositoryID3Tag @Inject constructor(
        val context: Context
) {
    @Throws(Exception::class)
    private fun getSongId3Tag(songDomain: SongDomain): SongTagDomain {
        val tag = AudioFileIO.read(File(songDomain.data)).tagOrCreateAndSetDefault
        return SongTagDomain(
                SongDomain(
                        -1,
                        songDomain.id,
                        tag.getFirst(FieldKey.TITLE),
                        songDomain.trackNumber,
                        songDomain.year,
                        songDomain.duration,
                        songDomain.data,
                        songDomain.dateModified,
                        songDomain.albumId,
                        tag.getFirst(FieldKey.ALBUM),
                        songDomain.artistId,
                        tag.getFirst(FieldKey.ARTIST)
                ),
                tag.getFirst(FieldKey.GENRE),
                tag.getFirst(FieldKey.YEAR),
                tag.getFirst(FieldKey.TRACK),
                tag.firstArtwork.binaryData
        )
    }

    @Throws(Exception::class)
    private fun updateSongId3Tag(song: SongTagDomain): SongTagDomain {
        val songDomain = song.songDomain
        val audioFile = AudioFileIO.read(File(songDomain.data))
        val tag = audioFile.tagOrCreateAndSetDefault
        tag.setField(FieldKey.TITLE, songDomain.title)
        tag.setField(FieldKey.ALBUM, songDomain.albumName)
        tag.setField(FieldKey.ARTIST, songDomain.artistName)
        tag.setField(FieldKey.TRACK, song.track)
        tag.setField(FieldKey.GENRE, song.genre)
        tag.setField(FieldKey.YEAR, song.year)
        try {
            audioFile.commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        MediaScannerConnection.scanFile(context.applicationContext, listOf(song.songDomain.data).toTypedArray(), null, null)
        return song
    }

    fun updateId3Tag(song: SongTagDomain): Observable<SongTagDomain> {
        return Observable.create { emitter -> emitter.onNext(updateSongId3Tag(song)) }
    }

    fun readId3Tag(song: SongDomain): Observable<SongTagDomain> {
        return Observable.create { emitter -> emitter.onNext(getSongId3Tag(song)) }
    }

    companion object {
        private val TAG = RepositoryID3Tag::class.java.simpleName
    }
}
