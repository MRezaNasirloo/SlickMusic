package com.mrezanasirloo.data.loader;

import android.content.Context;
import android.provider.MediaStore.Audio.AudioColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mrezanasirloo.data.SortOrder;
import com.mrezanasirloo.domain.model.AlbumDomain;
import com.mrezanasirloo.domain.model.SongDomain;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class AlbumLoader {

    public static String getSongLoaderSortOrder(Context context) {
        return SortOrder.AlbumSortOrder.ALBUM_A_Z + ", " + SortOrder.AlbumSongSortOrder.SONG_A_Z;
    }

    @NonNull
    public static ArrayList<AlbumDomain> getAllAlbums(@NonNull final Context context) {
        ArrayList<SongDomain> songDomains = SongLoader.getSongs(SongLoader.makeSongCursor(
                context,
                null,
                null,
                getSongLoaderSortOrder(context))
        );
        return splitIntoAlbums(songDomains);
    }

    @NonNull
    public static ArrayList<AlbumDomain> getAlbums(@NonNull final Context context, String query) {
        ArrayList<SongDomain> songDomains = SongLoader.getSongs(SongLoader.makeSongCursor(
                context,
                AudioColumns.ALBUM + " LIKE ?",
                new String[]{"%" + query + "%"},
                getSongLoaderSortOrder(context))
        );
        return splitIntoAlbums(songDomains);
    }

    @NonNull
    public static AlbumDomain getAlbum(@NonNull final Context context, int albumId) {
        ArrayList<SongDomain> songDomains = SongLoader.getSongs(SongLoader.makeSongCursor(context, AudioColumns.ALBUM_ID + "=?", new String[]{String.valueOf(albumId)}, getSongLoaderSortOrder(context)));
        AlbumDomain albumDomain = new AlbumDomain(songDomains);
        sortSongsByTrackNumber(albumDomain);
        return albumDomain;
    }

    @NonNull
    public static ArrayList<AlbumDomain> splitIntoAlbums(@Nullable final ArrayList<SongDomain> songDomains) {
        ArrayList<AlbumDomain> albumDomains = new ArrayList<>();
        if (songDomains != null) {
            for (SongDomain songDomain : songDomains) {
                getOrCreateAlbum(albumDomains, songDomain.getAlbumId()).getSongDomains().add(songDomain);
            }
        }
        for (AlbumDomain albumDomain : albumDomains) {
            sortSongsByTrackNumber(albumDomain);
        }
        return albumDomains;
    }

    private static AlbumDomain getOrCreateAlbum(ArrayList<AlbumDomain> albumDomains, int albumId) {
        for (AlbumDomain albumDomain : albumDomains) {
            if (!albumDomain.getSongDomains().isEmpty() && albumDomain.getSongDomains().get(0).getAlbumId() == albumId) {
                return albumDomain;
            }
        }
        AlbumDomain albumDomain = new AlbumDomain(new ArrayList<>(8));
        albumDomains.add(albumDomain);
        return albumDomain;
    }

    private static void sortSongsByTrackNumber(AlbumDomain albumDomain) {
        Collections.sort(albumDomain.getSongDomains(), (o1, o2) -> o1.getTrackNumber() - o2.getTrackNumber());
    }
}
