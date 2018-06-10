/*
 * Copyright (C) 2012 Andrew Neal Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.mrezanasirloo.data;

import android.provider.MediaStore;

/**
 * Holds all of the sort orders for each list type.
 *
 * @author Andrew Neal (andrewdneal@gmail.com)
 */
public final class SortOrder {

    /**
     * This class is never instantiated
     */
    public SortOrder() {
    }

    /**
     * ArtistDomain sort order entries.
     */
    public interface ArtistSortOrder {
        /* ArtistDomain sort order A-Z */
        String ARTIST_A_Z = MediaStore.Audio.Artists.DEFAULT_SORT_ORDER;

        /* ArtistDomain sort order Z-A */
        String ARTIST_Z_A = ARTIST_A_Z + " DESC";

        /* ArtistDomain sort order number of songDomains */
        String ARTIST_NUMBER_OF_SONGS = MediaStore.Audio.Artists.NUMBER_OF_TRACKS
                + " DESC";

        /* ArtistDomain sort order number of albumDomains */
        String ARTIST_NUMBER_OF_ALBUMS = MediaStore.Audio.Artists.NUMBER_OF_ALBUMS
                + " DESC";
    }

    /**
     * AlbumDomain sort order entries.
     */
    public interface AlbumSortOrder {
        /* AlbumDomain sort order A-Z */
        String ALBUM_A_Z = MediaStore.Audio.Albums.DEFAULT_SORT_ORDER;

        /* AlbumDomain sort order Z-A */
        String ALBUM_Z_A = ALBUM_A_Z + " DESC";

        /* AlbumDomain sort order songDomains */
        String ALBUM_NUMBER_OF_SONGS = MediaStore.Audio.Albums.NUMBER_OF_SONGS
                + " DESC";

        /* AlbumDomain sort order artist */
        String ALBUM_ARTIST = MediaStore.Audio.Artists.DEFAULT_SORT_ORDER
                + ", " + MediaStore.Audio.Albums.DEFAULT_SORT_ORDER;

        /* AlbumDomain sort order year */
        String ALBUM_YEAR = MediaStore.Audio.Media.YEAR + " DESC";
    }

    /**
     * SongDomain sort order entries.
     */
    public interface SongSortOrder {
        /* SongDomain sort order A-Z */
        String SONG_A_Z = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

        /* SongDomain sort order Z-A */
        String SONG_Z_A = SONG_A_Z + " DESC";

        /* SongDomain sort order artist */
        String SONG_ARTIST = MediaStore.Audio.Artists.DEFAULT_SORT_ORDER;

        /* SongDomain sort order album */
        String SONG_ALBUM = MediaStore.Audio.Albums.DEFAULT_SORT_ORDER;

        /* SongDomain sort order year */
        String SONG_YEAR = MediaStore.Audio.Media.YEAR + " DESC";

        /* SongDomain sort order duration */
        String SONG_DURATION = MediaStore.Audio.Media.DURATION + " DESC";

        /* SongDomain sort order date */
        String SONG_DATE = MediaStore.Audio.Media.DATE_ADDED + " DESC";
    }

    /**
     * AlbumDomain song sort order entries.
     */
    public interface AlbumSongSortOrder {
        /* AlbumDomain song sort order A-Z */
        String SONG_A_Z = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

        /* AlbumDomain song sort order Z-A */
        String SONG_Z_A = SONG_A_Z + " DESC";

        /* AlbumDomain song sort order track list */
        String SONG_TRACK_LIST = MediaStore.Audio.Media.TRACK + ", "
                + MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

        /* AlbumDomain song sort order duration */
        String SONG_DURATION = SongSortOrder.SONG_DURATION;
    }

    /**
     * ArtistDomain song sort order entries.
     */
    public interface ArtistSongSortOrder {
        /* ArtistDomain song sort order A-Z */
        String SONG_A_Z = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

        /* ArtistDomain song sort order Z-A */
        String SONG_Z_A = SONG_A_Z + " DESC";

        /* ArtistDomain song sort order album */
        String SONG_ALBUM = MediaStore.Audio.Media.ALBUM;

        /* ArtistDomain song sort order year */
        String SONG_YEAR = MediaStore.Audio.Media.YEAR + " DESC";

        /* ArtistDomain song sort order duration */
        String SONG_DURATION = MediaStore.Audio.Media.DURATION + " DESC";

        /* ArtistDomain song sort order date */
        String SONG_DATE = MediaStore.Audio.Media.DATE_ADDED + " DESC";
    }

    /**
     * ArtistDomain album sort order entries.
     */
    public interface ArtistAlbumSortOrder {
        /* ArtistDomain album sort order A-Z */
        String ALBUM_A_Z = MediaStore.Audio.Albums.DEFAULT_SORT_ORDER;

        /* ArtistDomain album sort order Z-A */
        String ALBUM_Z_A = ALBUM_A_Z + " DESC";

        /* ArtistDomain album sort order year */
        String ALBUM_YEAR = MediaStore.Audio.Media.YEAR
                + " DESC";

        /* ArtistDomain album sort order year */
        String ALBUM_YEAR_ASC = MediaStore.Audio.Media.YEAR
                + " ASC";
    }

    /**
     * Genre sort order entries.
     */
    public interface GenreSortOrder {
        /* Genre sort order A-Z */
        String GENRE_A_Z = MediaStore.Audio.Genres.DEFAULT_SORT_ORDER;

        /* Genre sort order Z-A */
        String ALBUM_Z_A = GENRE_A_Z + " DESC";
    }

}
