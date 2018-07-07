package com.mrezanasirloo.domain.implementation.repository;

import android.support.annotation.NonNull;

import com.mrezanasirloo.domain.model.SongDomain;
import com.mrezanasirloo.domain.model.SongTagDomain;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;

@Singleton
public class RepositoryID3Tag {
    private static final String TAG = RepositoryID3Tag.class.getSimpleName();

    @Inject
    public RepositoryID3Tag() {
    }

    @NonNull
    private AudioFile getAudioFile(@NonNull String path) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException {
        return AudioFileIO.read(new File(path));
    }

    private SongTagDomain getSongId3Tag(@NonNull SongDomain songDomain) throws ReadOnlyFileException, IOException, TagException, InvalidAudioFrameException, CannotReadException {
        Tag tag = getAudioFile(songDomain.getData()).getTagOrCreateAndSetDefault();
        return new SongTagDomain(
                new SongDomain(
                        songDomain.getId(),
                        tag.getFirst(FieldKey.TITLE),
                        Integer.valueOf(tag.getFirst(FieldKey.TRACK)),
                        Integer.valueOf(tag.getFirst(FieldKey.YEAR)),
                        songDomain.getDuration(),
                        songDomain.getData(),
                        songDomain.getDateModified(),
                        songDomain.getAlbumId(),
                        tag.getFirst(FieldKey.ALBUM),
                        songDomain.getArtistId(),
                        tag.getFirst(FieldKey.ARTIST)
                ),
                tag.getFirst(FieldKey.GENRE),
                tag.getFirstArtwork().getBinaryData()
        );
    }

    public Observable<SongTagDomain> readId3Tag(SongDomain song) {
        return Observable.create(emitter -> emitter.onNext(getSongId3Tag(song)));
    }
}
