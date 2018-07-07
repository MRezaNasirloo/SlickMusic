package com.mrezanasirloo.domain.implementation.repository

import com.mrezanasirloo.domain.model.SongDomain
import com.mrezanasirloo.domain.model.SongTagDomain
import io.reactivex.Observable
import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import javax.inject.Inject

class RepositoryID3Tag @Inject constructor() {

    @Throws(Exception::class)
    private fun getAudioFile(path: String): AudioFile {
        return AudioFileIO.read(File(path))
    }

    @Throws(Exception::class)
    private fun getSongId3Tag(songDomain: SongDomain): SongTagDomain {
        val tag = getAudioFile(songDomain.data).tagOrCreateAndSetDefault
        return SongTagDomain(
                SongDomain(
                        songDomain.id,
                        tag.getFirst(FieldKey.TITLE),
                        Integer.valueOf(tag.getFirst(FieldKey.TRACK)),
                        -1,
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
                tag.firstArtwork.binaryData
        )
    }

    fun readId3Tag(song: SongDomain): Observable<SongTagDomain> {
        return Observable.create { emitter -> emitter.onNext(getSongId3Tag(song)) }
    }

    companion object {
        private val TAG = RepositoryID3Tag::class.java.simpleName
    }
}
